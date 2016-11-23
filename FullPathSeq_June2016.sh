#!/bin/csh
# Created by: Chandra Sekhar Pedamallu @ DFCI, The Broad Inst.
# Date: June 2016
# Full PathSeq pipeline
# time $pdir/FullPathSeq_June2016.sh $bamloc/SN218_Run0771_Lane1_106LM_L_152911_BC6.demult.bam BAM $config_files/config.rnaseq.txt
#
set start_time=`date +%s`

@ noargs=$#

#############################PLEASE SET THESE ENVIRONMENTAL VARIABLES (BEFORE YOU RUN)- START###########################
# Program Settings
set Institute="BROAD"

# PathSeq installation or unzip location
set PathSeq_loc="/xchip/pasteur/chandra/FullPathSeq_Oct2015/V7"

# Temporary directory Location
set Tmp_dir="/broad/hptmp/"

# Java executable Location
set Java="/broad/software/free/Linux/redhat_5_x86_64/pkgs/oracle-java-jdk_1.7.0-51_x86_64/bin/java"

# BWA Location
set Bwa_loc="/broad/software/free/Linux/redhat_6_x86_64/pkgs/bwa_0.7.12/bwa"

# BLAST Location
set Blast_loc="/broad/software/free/Linux/redhat_5_x86_64/pkgs/ncbi-blast_2.2.30+-x86_64/bin/"

# Repeatmasker Location
set Repeatmasker_loc="/broad/software/free/Linux/redhat_6_x86_64/pkgs/repeatmasker-4.0.5/RepeatMasker/"

# Python executable Location
set Python="/broad/software/free/Linux/redhat_5_x86_64/pkgs/python_2.7.1-sqlite3-rtrees/bin/python"

# Location of Samtools
set Samtools="/broad/software/free/Linux/redhat_6_x86_64/pkgs/samtools/samtools_1.3/bin/samtools"

# Type of cluster
#set Cluster_type="STANDALONE" # UGER / LSF / SGE/ SBATCH / STANDALONE

# Assembler
set Assembler_loc="/xchip/koch/softwares/velvet_1.2.10/"

# Loading Python-2.7; Java-1.7; BLAST Aligner; BWA Aligner; Assembler (Velvet, Trinity)
set Package_loader="YES"
set Initial_loader="source /broad/software/scripts/useuse"
set Load_cluster="use UGER"
set Load_python="use Python-2.7"
set Load_java="use Java-1.7"

# Base Quality and Exception base quality
set base_quality="15"
set excep_base="3"
set offset="33"
#############################PLEASE SET THESE ENVIRONMENTAL VARIABLES (BEFORE YOU RUN)- END###########################

# PathSeq Java files location
set PathSeq_java=$PathSeq_loc"/Java"
set PathSeq_samjar=$PathSeq_loc"/3rdparty/sam-1.35.jar"

###################INPUT FILES###################################################################
set input_file = $1 # Input file (BAM or FQ1)
set treads = $2 # Type of Input file (BAM or FQ1)
set config_file=$3 # Location of Config file
set Cluster_type=$4 # UGER / LSF / SGE/ SBATCH / STANDALONE


if($noargs < 4) then
	echo "Please check your argunments"
	echo "Usage : ./FullPathSeq_xxxx.sh <Input file in BAM or FQ1 or Fasta format> <Type of reads i.e. FQ1/BAM/FASTA> <Config file> <UGER/LSF/SGE/SBATCH/STANDALONE>"
	echo "Example : ./FullPathSeq_xxxx.sh unmappedreads.10K.fq1 FQ1 config_cluster.txt"
	exit
endif

###################INPUT FILES###################################################################

# Present Directory
set pdir = `pwd`
rm $pdir"/clean.files"
rm -r $pdir"/Commands/"
rm -r $pdir"/Logs/"
mkdir $pdir"/Commands/"
mkdir $pdir"/Logs/"

# Random number generator
set num=`awk 'BEGIN { srand(); printf "%d",  rand()*10000;}' /dev/null`

if($Package_loader == "YES") then
	echo $Initial_loader > $pdir"/"$num".loader"
	echo $Load_cluster >> $pdir"/"$num".loader"
	echo $Load_python >> $pdir"/"$num".loader"
	echo $Load_java >> $pdir"/"$num".loader"
	chmod 777 $pdir"/"$num".loader"
	$pdir"/"$num".loader"
endif


echo "Step 1: Format change (if necessary) "$input_file

set reads = "qf_1.unique.fq1"
if($treads == "FASTA") then
	$Java -Xms1g -Xmx4g -classpath $PathSeq_java Fas2FQ1 $input_file $num".qf_1.input.fq1"

	echo " sort the file"
	sort +1 -2 -T $Tmp_dir -S 1G $num".qf_1.input.fq1" > $num".qf_1.sorted.fq1"

	echo "Remove the reads"
	$Java -classpath $PathSeq_java catalogueduplicatereads $num".qf_1.sorted.fq1" $num".qf_1.unique.fq1"
	rm $num".qf_1.input.fq1"
	rm $num".qf_1.sorted.fq1"
	set reads = $num".qf_1.unique.fq1"
else
	if($treads == "BAM") then
		
		#PathSeq_loc/qualityfilter_Oct2015.sh input_file base_quality excep_base
		echo $input_file
		echo $PathSeq_samjar
		echo $PathSeq_java
	
		echo "Running extract unmapped"
		$Java -Xms1g -Xmx4g -classpath $PathSeq_samjar":"$PathSeq_java getunmapped_test $input_file $num".unmapped.fq1"

		echo "Running quality filter"
		#java -Xmx4g -classpath $PathSeq_java QualFilter $num".unmapped.fq1" qf_1.fq1 $base_quality $excep_base fullLength 0 0 33
		
		java -Xmx4g -classpath $PathSeq_java QualFilter_August2016 $num".unmapped.fq1" $base_quality $excep_base $offset $num".qf_1.input.fq1"

		echo " sort the file"
		sort +1 -2 -T $Tmp_dir -S 1G $num".qf_1.input.fq1" > $num".qf_1.sorted.fq1"

		echo "Remove the reads"
		$Java -classpath $PathSeq_java catalogueduplicatereads $num".qf_1.sorted.fq1" $num".qf_1.unique.fq1"
	
		rm $num".qf_1.input.fq1"
		rm $num".qf_1.sorted.fq1"
		
		set reads = $num".qf_1.unique.fq1"
	else
		if($treads == "FQ1") then
			echo "Step 1: Check the FQ1 file"
			echo "Running quality filter"
			#java -Xmx4g -classpath $PathSeq_java QualFilter $input_file qf_1.fq1 $base_quality $excep_base fullLength 0 0 33
			java -Xmx4g -classpath $PathSeq_java QualFilter_August2016 $input_file $base_quality $excep_base $offset $num".qf_1.input.fq1"


			echo " sort the file"
			sort +1 -2 -T $Tmp_dir -S 1G $num".qf_1.input.fq1" > $num".qf_1.sorted.fq1"

			echo "Remove the reads"
			$Java -classpath $PathSeq_java catalogueduplicatereads $num".qf_1.sorted.fq1" $num".qf_1.unique.fq1"
			
			rm $num".qf_1.input.fq1"
			rm $num".qf_1.sorted.fq1"
			set reads = $num".qf_1.unique.fq1"

		else
			if($treads == "FASTQ") then
				echo "Step 1: Check the FASTQ file"
				$Java  -Xms1g -Xmx4g -classpath $PathSeq_java Fastq2FQone $input_file $num".qf_1.input.fq1"

				
				java -Xmx4g -classpath $PathSeq_java QualFilter_August2016 $num".qf_1.input.fq1" $base_quality $excep_base $offset $num".qf_1.fq1"
				
				echo " sort the file"
				sort +1 -2 -T $Tmp_dir -S 1G $num".qf_1.fq1" > $num".qf_1.sorted.fq1"

				echo "Remove the reads"
				$Java -classpath $PathSeq_java catalogueduplicatereads $num".qf_1.sorted.fq1" $num".qf_1.unique.fq1"
				rm $num".qf_1.input.fq1"
				rm $num".qf_1.fq1"
				rm $num".qf_1.sorted.fq1"
				set reads = $num".qf_1.unique.fq1"
			endif
		endif			
	
	endif
endif


set no_configs=`awk -f $PathSeq_loc/config_reader.awk T0=$config_file T01=$num".current" T02=$num".configlst" T03=$pdir < $PathSeq_loc/empty.awk`
rm $num".command"
if($Package_loader == "YES") then
	echo "Launched Job Submission started"
	cp $pdir/$num.loader $num".command"
	#echo $pdir/$num.loader > $num".command"
	echo "python "$PathSeq_loc"/jobsubmission.py "$reads" "$pdir"/"$num".current "$pdir"/"$num".configlst "$Cluster_type" "$pdir" 1 "$Institute" "$PathSeq_loc" "$Tmp_dir" "$Java" "$Bwa_loc" "$Blast_loc" "$Repeatmasker_loc" "$Python" "$Package_loader" "$pdir"/"$num".loader "$Assembler_loc" "$config_file" "$reads $Samtools>> $num".command"
	chmod 777 $num".command"
	./$num".command"
	echo "Launched Job Submission completed"
else
	echo "Launched Job Submission started"
	echo "python "$PathSeq_loc"/jobsubmission.py "$reads" "$pdir"/"$num".current "$pdir"/"$num".configlst "$Cluster_type" "$pdir" 1 "$Institute" "$PathSeq_loc" "$Tmp_dir" "$Java" "$Bwa_loc" "$Blast_loc" "$Repeatmasker_loc" "$Python" NO NA "$Assembler_loc" "$config_file" "$reads $Samtools > $num".command"
	chmod 777 $num".command"
	./$num".command"
	echo "Launched Job Submission completed"
endif



set end_time=`date +%s`
set time_taken=`awk 'BEGIN {print (ARGV[2]-ARGV[1]); } ' $start_time $end_time /dev/null`

echo "Time Taken (in seconds) : "$time_taken
