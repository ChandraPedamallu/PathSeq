#!/bin/csh
# Created by: Chandra Sekhar Pedamallu @ DFCI, The Broad Inst.
# Date: Feb2016
# Integration events
# 

#############################PLEASE SET THESE ENVIRONMENTAL VARIABLES (BEFORE YOU RUN)- START###########################
# Program Settings
set Institute="BROAD"

# PathSeq installation or unzip location
set PathSeq_loc="/xchip/pasteur/chandra/FullPathSeq_Oct2015/V7"

# Temporary directory Location
set Tmp_dir="/broad/hptmp/"

# Java executable Location
set Java="/broad/software/free/Linux/redhat_5_x86_64/pkgs/oracle-java-jdk_1.7.0-51_x86_64/bin/java"

#Samtools
set samtoolsLoc="/broad/software/free/Linux/redhat_5_x86_64/pkgs/samtools/samtools_0.1.19/bin/samtools"

#Tophat2
set Tophat="/broad/software/free/Linux/redhat_6_x86_64/pkgs/tophat_2.0.14/bin/tophat2"

set BWA="/broad/software/free/Linux/redhat_6_x86_64/pkgs/bwa_0.7.12/bwa"
set SAMBLASTER="/xchip/pasteur/chandra/FullPathSeq_Oct2015/V7/AdditionalTools/Integration_Events/software/samblaster/samblaster"
set LUMPY="/xchip/pasteur/chandra/FullPathSeq_Oct2015/V7/AdditionalTools/Integration_Events/software/lumpy-sv/bin/lumpy"
set LUMPY_DIST="/xchip/pasteur/chandra/FullPathSeq_Oct2015/V7/AdditionalTools/Integration_Events/software/lumpy-sv/scripts/pairend_distro.py"

# Loading Python-2.7; Java-1.7; BLAST Aligner; BWA Aligner; Assembler (Velvet, Trinity)
set Package_loader="YES"
set Initial_loader="source /broad/software/scripts/useuse"
set Load_java="use Java-1.7"
set Load_tophat="use .tophat-2.0.14"
set Load_bowtie="use Bowtie2"
set Load_samtools="use .samtools-0.1.9"
set Load_BWA="use BWA"
set Load_Numpy="use .numpy-1.9.1-python-2.7.1-sqlite3-rtrees"
#############################PLEASE SET THESE ENVIRONMENTAL VARIABLES (BEFORE YOU RUN)- END###########################

# PathSeq Java files location
set PathSeqIntegration_java=$PathSeq_loc"/AdditionalTools/Integration_Events/Java"
set PathSeq_samjar=$PathSeq_loc"/3rdparty/sam-1.35.jar"
set searchExp = "PAPILLOMA\|HPV" #PAPILLOMA\|HPV--- Please edit this if you are looking for another virus

###################INPUT FILES###################################################################
set hitTable = $1 # Hittable with mapping to viruses and microbes
set stillUnmappedreads = $2
set locDatabase = $3 # Location of database
set oBAM = $4
set libraryType = $5 #DNASEQ / RNASEQ
set read_length = $6

# if original BAM is SPECIAL - UNC Bams from CGHUB needs special treatment
set typeOBAM = $7 # Sequence center --- UNC-SPECIAL / DEFAULT

# Present Directory
set pdir = `pwd`

# Random number generator
set num=`awk 'BEGIN { srand(); printf "%d",  rand()*10000;}' /dev/null`
if ($Package_loader == "YES") then
	echo $Initial_loader > $pdir"/"$num".loader"
	#echo $Load_cluster >> $pdir"/"$num".loader"
	echo $Load_java >> $pdir"/"$num".loader"
	echo $Load_tophat >> $pdir"/"$num".loader"
	echo $Load_bowtie >> $pdir"/"$num".loader"
	echo $Load_samtools >> $pdir"/"$num".loader"
	echo $Load_BWA >> $pdir"/"$num".loader"
	echo $Load_Numpy >> $pdir"/"$num".loader"
	chmod 777 $pdir"/"$num".loader"
	echo $pdir"/"$num".loader"
	$pdir"/"$num".loader"
endif

echo "Step 1: Extract the read names that maps to : " $searchExp
echo $searchExp $hitTable
grep -i "$searchExp" $hitTable | awk 'BEGIN{FS="\t";}{if($3==1){ns1=split($1, x, "/"); ns1=split(x[1], xx, "_"); if(ns1>2){print xx[1]"_"xx[2];}else{print xx[1];}}}' | sort -u > selected.readnames.txt
echo "Step 2: Combine with stillunmapped reads"
if ($stillUnmappedreads == "NULL") then
	cat selected.readnames.txt | sort -u > all.readnames.txt
else
	echo "Step 2: Look for the stillunmapped reads " $stillUnmappedreads
	cat $stillUnmappedreads | awk 'BEGIN{FS="\t";}{ns1=split($1, x, "/"); ns=split(x[1], xx, "_");print xx[1];}' | sort -u > stillunmapped.readnames.txt
	cat selected.readnames.txt stillunmapped.readnames.txt | sort -u > all.readnames.txt
endif

echo "Step 3: Extract the read pairs based on "$searchExp" "
if ($typeOBAM == "SPECIAL") then
	echo "Step 3a: Convert BAM to SAM file"
	$samtoolsLoc view $oBAM > "samfile.txt"

	echo "Step 3b: Extract pairs from SAMFile"

	$Java -classpath $PathSeqIntegration_java extractPairs_UNCfile_July2014 all.readnames.txt samfile.txt readpairs.fq1
	sort readpairs.fq1 > readpairs.sorted.fq1
	
	# Create the forward and reverse reads
	# Forward
	cat readpairs.sorted.fq1 | awk '{if(I==0){I=1;}else if(I==1){print "@"$1"\n"$10"\n+\n"$11;I=0;}}' > reads_2.fastq
	# Reverse
	cat readpairs.sorted.fq1 | awk '{if(I==0){I=1;print "@"$1"\n"$10"\n+\n"$11}else if(I==1){I=0;}}' > reads_1.fastq
else
	$Java -Xms128m -XX:-UseGCOverheadLimit -classpath $PathSeq_samjar":"$PathSeqIntegration_java extractPairs_BAM all.readnames.txt $oBAM readpairs.fq1
	sort readpairs.fq1 > readpairs.sorted.fq1
	
	# Create the forward and reverse reads
	# Forward
	cat readpairs.sorted.fq1 | awk '{if(I==0){I=1;}else if(I==1){print "@"$1"\n"$2"\n+\n"$4;I=0;}}' > reads_2.fastq
	# Reverse
	cat readpairs.sorted.fq1 | awk '{if(I==0){I=1;print "@"$1"\n"$2"\n+\n"$4}else if(I==1){I=0;}}' > reads_1.fastq
endif


if ($libraryType == "RNASEQ") then
	$Tophat -o tophat_out -p 4 --mate-inner-dist 300 --mate-std-dev 500 --fusion-search --b2-very-sensitive $locDatabase reads_1.fastq reads_2.fastq
	
	cat $PathSeq_loc/AdditionalTools/Integration_Events/Header_RNA.txt $pdir/tophat_out/fusions.out > $pdir/Integration_events.txt
else
	if ($libraryType == "DNASEQ") then
		$BWA mem $locDatabase reads_1.fastq reads_2.fastq | $SAMBLASTER -e -d samp.disc.sam -s samp.split.sam | $samtoolsLoc view -Sb - > samp.out.bam
		$samtoolsLoc view -r readgroup1 samp.out.bam | tail -n+100000 | $LUMPY_DIST -r 101 -X 4 -N 10000 -o samp.lib1.histo
		
		
		cat samp.disc.sam | perl -e 'while(<STDIN>){if(/^@/){print; next;} chomp; print; print "\tRG:Z:1\n"}' | $samtoolsLoc view -bS - > samp.disc.bam
		cat samp.split.sam | perl -e 'while(<STDIN>){if(/^@/){print; next;} chomp; print; print "\tRG:Z:1\n"}' | $samtoolsLoc view -bS - > samp.split.bam
		$samtoolsLoc sort samp.disc.bam samp.disc.sorted 
		$samtoolsLoc sort samp.split.bam samp.split.sorted

		echo "$LUMPY" -mw 4 -tt 0 -pe id:sample,bam_file:samp.disc.sorted.bam,histo_file:samp.lib1.histo,mean:500,stdev:50,read_length:"$read_length",min_non_overlap:"$read_length",discordant_z:5,back_distance:10,weight:1,min_mapping_threshold:20 -sr id:sample,bam_file:samp.split.sorted.bam,back_distance:10,weight:1,min_mapping_threshold:20" > structural.vcf" > run.command
		chmod 777 run.command
		./run.command
		
		cp $pdir/structural.vcf $pdir/Integration_events.txt

	endif
endif

if ($typeOBAM == "SPECIAL") then
	rm samfile.txt
endif
