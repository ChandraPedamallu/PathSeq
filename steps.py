#!/usr/bin/env python
# Created: Chandra Sekhar Pedamallu, DFCI, The Broad Institute
# Email : pcs.murali@gmail.com
# Purpose: PathSeq2.0 pipeline
# Updates: Steps involved in the pipeline
# DFCI / Broad Institute@ copyright

import sys
import os
import commands
import random
import time
import shutil
import glob

start_time = time.time()

# Call a Read Mapped versus unmapped"
premega_thres=" 0.9 0.95 "
blastn_thres=" 1E-7 "
megablast_thres=" 1E-7 "
blastx_thres=" 0.01 "
hash_length="21"
minlength_contigs="75"
# Call a Read Mapped versus unmapped"


print "PATHSEQ PIPELINE RUNS\n"
print "STEPs*********************"

#Arguments
args=sys.argv
print "Step 0: Read config, premegablast, megablast, and blastn config files"
# Strip off spaces infornt and behing the lines and get file name
namefile = args[1].strip() # Read in FQ1 format
configfile = args[2].strip()
nthreads = args[3].strip()
nextconfiglist = args[4].strip()
pdir=args[5].strip()
cdir=args[6].strip()
full_file=args[7].strip()
total_split=args[8].strip()
id_step=args[9].strip()
compute=args[10].strip()
namefile_o=args[11].strip()
print configfile

# Program Settings
Institute=args[12].strip()
# PathSeq installation or unzip location
PathSeq_loc=args[13].strip()
PathSeq_java=PathSeq_loc + "/Java"
# Temporary directory Location
Tmp_dir=args[14].strip()
# Java library Location
Java=args[15].strip()
# BWA Location
Bwa_loc=args[16].strip()
# BLAST Location
Blast_loc=args[17].strip()
# Repeatmasker Location
Repeatmasker_loc=args[18].strip()
# Python Location
Python=args[19].strip()
# Loader_package
Package_loader=args[20].strip()
# Loader Location
Loader_file=args[21].strip()
# Assembler location
Assembler_loc=args[22].strip()
# Original Configfile
O_config=args[23].strip()
# Original Inputfile
O_inputfile=args[24].strip()
# Original Inputfile
Samtools=args[25].strip()

mergesamjar=PathSeq_loc + "/3rdparty/MergeSamFiles.jar"

# Run the loader file
if Package_loader == "YES":
	print Loader_file
	loader_cmd=commands.getstatusoutput(Loader_file)
	print loader_cmd


ff = open(configfile, 'r')
database = ff.readlines()
ff.close()

dbindex=0
print "Hello"

print "Statistics before config on the partition"
stat="wc -l " + namefile
stat=stat + " > "
stat=stat + namefile
stat=stat + "."
stat=stat + str(dbindex)
stat=stat + ".stat"
print stat
stat_cmd=commands.getstatusoutput(stat)
print stat_cmd

# Write the respective database file into config files and upload them
for no_databases1 in database:
	dbindex = dbindex + 1
	line = no_databases1.strip()
	data_split=line.split(":")
	print data_split
	
	if data_split[0] == "BWA":
		print "BWA";
		
		# Convert the FQ1 to Fastq
		fq1_2_fastq = Java + " -classpath "
		fq1_2_fastq = fq1_2_fastq + PathSeq_java 
		fq1_2_fastq = fq1_2_fastq + " FQone2Fastq " 
		fq1_2_fastq = fq1_2_fastq + namefile
		fq1_2_fastq = fq1_2_fastq + " "
		fq1_2_fastq = fq1_2_fastq + namefile
		fq1_2_fastq = fq1_2_fastq + ".fastq"
		print fq1_2_fastq
		fq1_2_fastq_cmd=commands.getstatusoutput(fq1_2_fastq)
		print fq1_2_fastq_cmd		

		# Run BWA alignment Step1
		bwa_aln = Bwa_loc + " aln "
		bwa_aln = bwa_aln + "-t "
		bwa_aln = bwa_aln + nthreads
		bwa_aln = bwa_aln + " "
		bwa_aln = bwa_aln + data_split[1]
		bwa_aln = bwa_aln + " "
		bwa_aln = bwa_aln + namefile
		bwa_aln = bwa_aln + ".fastq"
		bwa_aln = bwa_aln + " > "
		bwa_aln = bwa_aln + namefile
		bwa_aln = bwa_aln + ".aln.sai"
		print bwa_aln
		bwa_aln_cmd=commands.getstatusoutput(bwa_aln)
		print bwa_aln_cmd
		
		# Run BWA alignment Step2
		bwa_aln = Bwa_loc + " samse "
		bwa_aln = bwa_aln + data_split[1]
		bwa_aln = bwa_aln + " "
		bwa_aln = bwa_aln + namefile
		bwa_aln = bwa_aln + ".aln.sai "
		bwa_aln = bwa_aln + namefile
		bwa_aln = bwa_aln + ".fastq"
		bwa_aln = bwa_aln + " > "
		bwa_aln = bwa_aln + namefile
		bwa_aln = bwa_aln + "."
		bwa_aln = bwa_aln + str(id_step)
		bwa_aln = bwa_aln + "_"
		bwa_aln = bwa_aln + str(dbindex)
		bwa_aln = bwa_aln + ".aln.sam"
		print bwa_aln
		bwa_aln_cmd=commands.getstatusoutput(bwa_aln)
		print bwa_aln_cmd

		# Run Extract Unmapped reads
		extract_unmapped = Java + " -classpath "
		extract_unmapped = extract_unmapped + PathSeq_java
		extract_unmapped = extract_unmapped + " BWAunmapped_June2016 " 
		extract_unmapped = extract_unmapped + namefile
		extract_unmapped = extract_unmapped + " "
		extract_unmapped = extract_unmapped + namefile
		extract_unmapped = extract_unmapped + "."
		extract_unmapped = extract_unmapped + str(id_step)
		extract_unmapped = extract_unmapped + "_"
		extract_unmapped = extract_unmapped + str(dbindex)
		extract_unmapped = extract_unmapped + ".aln.sam "
		extract_unmapped = extract_unmapped + namefile
		extract_unmapped = extract_unmapped + ".tmp"
		print extract_unmapped
		extract_unmapped_cmd=commands.getstatusoutput(extract_unmapped)
		print extract_unmapped_cmd
		
		# Copy the unmapped reads 
		copy="mv "+ namefile
		copy=copy + ".tmp "
		copy=copy + namefile
		print copy
		copy_cmd=commands.getstatusoutput(copy)
		print copy_cmd		
		
		# Copy the unmapped reads 
		copy="cp "+ namefile
		copy=copy + " "
		copy=copy + namefile
		copy=copy + ".unmappedbwa.fq1."
		copy=copy + str(id_step)
		copy=copy + "_"
		copy=copy + str(dbindex)
		print copy
		copy_cmd=commands.getstatusoutput(copy)
		print copy_cmd		
		
		print "Statistics after BWA Step"
		stat="wc -l < "+ namefile
		stat=stat + ".unmappedbwa.fq1."
		stat=stat + str(id_step)
		stat=stat + "_"
		stat=stat + str(dbindex)
		stat=stat + " > "
		stat=stat + namefile
		stat=stat + ".bwa."
		stat=stat + str(id_step)
		stat=stat + "_"		
		stat=stat + str(dbindex)
		stat=stat + ".stat"
		print stat
		stat_cmd=commands.getstatusoutput(stat)
		print stat_cmd		
		

	elif data_split[0] == "MEGABLAST":
		print "MEGABLAST";
		# Convert the FQ1 to Fasta
		fq1_2_fasta = Java + " -classpath "
		fq1_2_fasta = fq1_2_fasta + PathSeq_java 
		fq1_2_fasta = fq1_2_fasta + " FQone2Fasta " 
		fq1_2_fasta = fq1_2_fasta + namefile
		fq1_2_fasta = fq1_2_fasta + " "
		fq1_2_fasta = fq1_2_fasta + namefile
		fq1_2_fasta = fq1_2_fasta + ".fasta"
		print fq1_2_fasta
		fq1_2_fasta_cmd=commands.getstatusoutput(fq1_2_fasta)
		print fq1_2_fasta_cmd

		# Megablast on reads 
		mega=Blast_loc + "blastn -task megablast -query " 
		mega=mega + namefile
		mega=mega + ".fasta -db \""
		mega=mega + data_split[1]
		mega=mega + "\" -outfmt 5 -evalue 0.0000001 -word_size 16 -max_target_seqs 5 -dust no -num_threads "
		mega=mega + nthreads
		mega=mega + " -out "
		mega=mega + namefile
		mega=mega + ".mega.out"
		print mega
		mega_cmd=commands.getstatusoutput(mega)
		print mega_cmd

		# Run Blastxml
		xml=Java + " -classpath "
		xml=xml + PathSeq_java
		xml=xml + " blastxml "
		xml=xml + namefile
		xml=xml + ".mega.out "
		xml=xml + namefile
		xml=xml + ".hit"
		print xml
		xml_cmd=commands.getstatusoutput(xml)
		print xml_cmd

		# create full query from the original reads and update Hit table
		exqfull=Java + " -classpath "
		exqfull=exqfull + PathSeq_java
		exqfull=exqfull + " extractFullQuert4BHitTable " 
		exqfull=exqfull +  namefile
		exqfull=exqfull + " "
		exqfull=exqfull + namefile
		exqfull=exqfull + ".hit "
		exqfull=exqfull + namefile
		exqfull=exqfull + ".mega.hittable."
		exqfull=exqfull + str(id_step)
		exqfull=exqfull + "_"
		exqfull=exqfull + str(dbindex)
		print exqfull
		exqfull_cmd=commands.getstatusoutput(exqfull)
		print exqfull_cmd

		# annotate the Hittable
		annotate=Java+ " -classpath "
		annotate=annotate + PathSeq_java 
		annotate=annotate + " annotate_hittable " 
		annotate=annotate + PathSeq_java
		annotate=annotate + "/names.dmp "
		annotate=annotate + PathSeq_java
		annotate=annotate + "/nodes.dmp "
		annotate=annotate + namefile
		annotate=annotate + ".mega.hittable."
		annotate=annotate + str(id_step)
		annotate=annotate + "_"
		annotate=annotate + str(dbindex)
		annotate=annotate + " "
		annotate=annotate + namefile
		annotate=annotate + ".mega.annotate.hittable."
		annotate=annotate + str(id_step)
		annotate=annotate + "_"			
		annotate=annotate + str(dbindex)
		print annotate
		annotate_cmd=commands.getstatusoutput(annotate)
		print annotate_cmd

		# Sorting the file"
		sort="sort +1 -2 -T " + Tmp_dir
		sort=sort + " "
		sort=sort + namefile
		sort=sort + ".mega.annotate.hittable."
		sort=sort + str(id_step)
		sort=sort + "_"			
		sort=sort + str(dbindex)
		sort=sort + " > "
		sort=sort + namefile
		sort=sort + ".mega.sort.tmp."
		sort=sort + str(id_step)
		sort=sort + "_"
		sort=sort + str(dbindex)
		print sort
		sort_cmd=commands.getstatusoutput(sort)
		print sort_cmd

		# Extract unmapped reads from the Hit table
		unmap=Java + " -classpath "
		unmap=unmap + PathSeq_java
		unmap=unmap + " extractUnmapped_newlatest " 
		unmap=unmap + namefile
		unmap=unmap + ".mega.sort.tmp."
		unmap=unmap + str(id_step)		
		unmap=unmap + "_"
		unmap=unmap + str(dbindex)		
		unmap=unmap + megablast_thres
		unmap=unmap + namefile
		unmap=unmap + " "
		unmap=unmap + namefile
		unmap=unmap + ".unmappedmega.fq1."
		unmap=unmap + str(id_step)		
		unmap=unmap + "_"			
		unmap=unmap + str(dbindex)
		unmap=unmap + " "
		unmap=unmap + namefile
		unmap=unmap + ".mappedmega.fq1."
		unmap=unmap + str(id_step)		
		unmap=unmap + "_"
		unmap=unmap + str(dbindex)
		print unmap
		unmap_cmd=commands.getstatusoutput(unmap)
		print unmap_cmd

		# Copy the unmapped reads to original file for running next round of database
		copy="cp "+ namefile
		copy=copy + ".unmappedmega.fq1."
		copy=copy + str(id_step)
		copy=copy + "_"
		copy=copy + str(dbindex)
		copy=copy + " " 
		copy=copy + namefile
		print copy
		copy_cmd=commands.getstatusoutput(copy)
		print copy_cmd

		print "Statistics after Megablast Step"
		stat="wc -l " + namefile
		stat=stat + " > "
		stat=stat + namefile
		stat=stat + ".mega."
		stat=stat + str(id_step)
		stat=stat + "_"
		stat=stat + str(dbindex)
		stat=stat + ".stat"
		print stat
		stat_cmd=commands.getstatusoutput(stat)
		print stat_cmd			
				
	elif data_split[0] == "BLASTN":
		print "BLASTN";				
		# Convert the FQ1 to Fasta
		fq1_2_fasta = Java + " -classpath "
		fq1_2_fasta = fq1_2_fasta + PathSeq_java
		fq1_2_fasta = fq1_2_fasta + " FQone2Fasta " 
		fq1_2_fasta = fq1_2_fasta + namefile
		fq1_2_fasta = fq1_2_fasta + " "
		fq1_2_fasta = fq1_2_fasta + namefile
		fq1_2_fasta = fq1_2_fasta + ".fasta"
		print fq1_2_fasta
		fq1_2_fasta_cmd=commands.getstatusoutput(fq1_2_fasta)
		print fq1_2_fasta_cmd


		blastn=Blast_loc + "blastn -task blastn -query " 
		blastn=blastn + namefile
		blastn=blastn + ".fasta -db \""
		blastn=blastn + data_split[1]
		blastn=blastn + "\" -outfmt 5 -evalue 0.0000001 -reward 1 -penalty -3 -gapopen 5 -gapextend 2 -dust no -max_target_seqs 5 -num_threads "
		blastn=blastn + nthreads
		blastn=blastn + " -out "
		blastn=blastn + namefile
		blastn=blastn + ".blastn.out"
		print blastn
		blastn_cmd=commands.getstatusoutput(blastn)
		print blastn_cmd

		# Run Blastxml
		xml=Java + " -classpath "
		xml=xml +PathSeq_java
		xml=xml + " blastxml "
		xml=xml + namefile
		xml=xml + ".blastn.out "
		xml=xml + namefile
		xml=xml + ".hit"
		print xml
		xml_cmd=commands.getstatusoutput(xml)
		print xml_cmd

		# create full query from the original reads and update Hit table
		exqfull=Java + " -classpath "
		exqfull=exqfull + PathSeq_java
		exqfull=exqfull + " extractFullQuert4BHitTable " 
		exqfull=exqfull +  namefile
		exqfull=exqfull + " "
		exqfull=exqfull + namefile
		exqfull=exqfull + ".hit "
		exqfull=exqfull + namefile
		exqfull=exqfull + ".blastn.hittable."
		exqfull=exqfull + str(id_step)
		exqfull=exqfull + "_"
		exqfull=exqfull + str(dbindex)
		print exqfull
		exqfull_cmd=commands.getstatusoutput(exqfull)
		print exqfull_cmd

		# annotate the Hittable
		annotate=Java+ " -classpath "
		annotate=annotate + PathSeq_java
		annotate=annotate + " annotate_hittable " 
		annotate=annotate + PathSeq_java
		annotate=annotate + "/names.dmp "
		annotate=annotate + PathSeq_java
		annotate=annotate + "/nodes.dmp "
		annotate=annotate + namefile
		annotate=annotate + ".blastn.hittable."
		annotate=annotate + str(id_step)
		annotate=annotate + "_"
		annotate=annotate + str(dbindex)
		annotate=annotate + " "
		annotate=annotate + namefile
		annotate=annotate + ".blastn.annotate.hittable."
		annotate=annotate + str(id_step)
		annotate=annotate + "_"
		annotate=annotate + str(dbindex)
		print annotate
		annotate_cmd=commands.getstatusoutput(annotate)
		print annotate_cmd

		# Sorting the file"
		sort="sort +1 -2 -T " + Tmp_dir
		sort=sort + " "
		sort=sort + namefile
		sort=sort + ".blastn.annotate.hittable."
		sort=sort + str(id_step)
		sort=sort + "_"
		sort=sort + str(dbindex)
		sort=sort + " > "
		sort=sort + namefile
		sort=sort + ".blastn.sort.tmp."
		sort=sort + str(id_step)
		sort=sort + "_"
		sort=sort + str(dbindex)
		print sort
		sort_cmd=commands.getstatusoutput(sort)
		print sort_cmd

		# Extract unmapped reads from the Hit table
		unmap=Java + " -classpath "
		unmap=unmap + PathSeq_java
		unmap=unmap + " extractUnmapped_newlatest " 
		unmap=unmap + namefile
		unmap=unmap + ".blastn.sort.tmp."
		unmap=unmap + str(id_step)		
		unmap=unmap + "_"
		unmap=unmap + str(dbindex)		
		unmap=unmap + blastn_thres
		unmap=unmap + namefile
		unmap=unmap + " "
		unmap=unmap + namefile
		unmap=unmap + ".unmappedblastn.fq1."
		unmap=unmap + str(id_step)		
		unmap=unmap + "_"				
		unmap=unmap + str(dbindex)
		unmap=unmap + " "
		unmap=unmap + namefile
		unmap=unmap + ".mappedblastn.fq1."
		unmap=unmap + str(id_step)		
		unmap=unmap + "_"				
		unmap=unmap + str(dbindex)
		print unmap
		unmap_cmd=commands.getstatusoutput(unmap)
		print unmap_cmd


		# Copy the unmapped reads to original file for running next round of database
		copy="cp "+ namefile
		copy=copy + ".unmappedmega.fq1."
		copy=copy + str(id_step)
		copy=copy + "_"
		copy=copy + str(dbindex)
		copy=copy + " " 
		copy=copy + namefile
		print copy
		copy_cmd=commands.getstatusoutput(copy)
		print copy_cmd		

		print "Statistics after BLASTN Step"
		stat="wc -l " + namefile
		stat=stat + " > "
		stat=stat + namefile
		stat=stat + ".blastn."				
		stat=stat + str(id_step)
		stat=stat + str("_")
		stat=stat + str(dbindex)
		stat=stat + ".stat"
		print stat
		stat_cmd=commands.getstatusoutput(stat)
		print stat_cmd			

				
	elif data_split[0] == "REPEATMASKER":
		print "REPEATMASKER";
		# Convert the FQ1 to Fasta
		fq1_2_fasta = Java + " -classpath "
		fq1_2_fasta = fq1_2_fasta + PathSeq_java
		fq1_2_fasta = fq1_2_fasta + " FQone2Fasta_RepeatMasker " 
		fq1_2_fasta = fq1_2_fasta + namefile
		fq1_2_fasta = fq1_2_fasta + " "
		fq1_2_fasta = fq1_2_fasta + namefile
		fq1_2_fasta = fq1_2_fasta + ".fasta"
		print fq1_2_fasta
		fq1_2_fasta_cmd=commands.getstatusoutput(fq1_2_fasta)
		print fq1_2_fasta_cmd

		# Running Repeatmasker
		repmask=Repeatmasker_loc + "RepeatMasker"
		repmask=repmask +" -no_is -pa "
		repmask=repmask + nthreads
		repmask=repmask + " -species vertebrates " 
		repmask=repmask + namefile
		repmask=repmask + ".fasta"
		print repmask
		repmask_cmd=commands.getstatusoutput(repmask)
		print repmask_cmd

		# Find repeatmasker file
		repfile=namefile + ".fasta.masked"
		print repfile
		repfile_cmd=os.path.exists(repfile)
		print repfile_cmd		


		if repfile_cmd:   # Masked file is present

			#Convert Repeatmaskerread.java file
			#Remove the sequence with more N's
			repread=Java + " -classpath "
			repread=repread + PathSeq_java
			repread=repread + " RepeatMaskerRead " 
			repread=repread + namefile
			repread=repread + ".fasta.masked"
			repread=repread + " "
			repread=repread + namefile
			repread=repread + " "
			repread=repread + namefile
			repread=repread + ".new.fq1"
			repread=repread + " 2"
			print repread
			repread_cmd=commands.getstatusoutput(repread)
			print repread_cmd

			copy="cp " + namefile
			copy=copy + ".new.fq1 "
			copy=copy + namefile
			print copy
			copy_cmd=commands.getstatusoutput(copy)
			print copy_cmd


		else:	# no masking present
			print "Nothing to be done"

		copy="cp "+ namefile
		copy=copy + " "
		copy=copy + namefile
		copy=copy + ".afterrep.fq1"
		print copy
		copy_cmd=commands.getstatusoutput(copy)
		print copy_cmd

		copy="cp " + namefile
		copy=copy + " "
		copy=copy + namefile
		copy=copy + ".unmappedrepeatmasker.fq1."
		copy=copy + str(id_step)
		copy=copy + "_"
		copy=copy + str(dbindex)
		print copy
		copy_cmd=commands.getstatusoutput(copy)
		print copy_cmd

		print "Statistics after REPEATMASKER Step"
		stat="wc -l " + namefile
		stat=stat + " > "
		stat=stat + namefile
		stat=stat + ".repeatmasker."					
		stat=stat + str(dbindex)
		stat=stat + ".stat"
		print stat
		stat_cmd=commands.getstatusoutput(stat)
		print stat_cmd		

	elif data_split[0] == "PREMEGABLAST":
		print "PREMEGABLAST";
		# Convert the FQ1 to Fasta
		fq1_2_fasta = Java + " -classpath "
		fq1_2_fasta = fq1_2_fasta + PathSeq_java
		fq1_2_fasta = fq1_2_fasta + " FQone2Fasta " 
		fq1_2_fasta = fq1_2_fasta + namefile
		fq1_2_fasta = fq1_2_fasta + " "
		fq1_2_fasta = fq1_2_fasta + namefile
		fq1_2_fasta = fq1_2_fasta + ".fasta"
		print fq1_2_fasta
		fq1_2_fasta_cmd=commands.getstatusoutput(fq1_2_fasta)
		print fq1_2_fasta_cmd

		#Pre-Megablast on reads 
		mega=Blast_loc + "blastn -task megablast -query " 
		mega=mega + namefile
		mega=mega + ".fasta -db \""
		mega=mega + data_split[1]
		mega=mega + "\" -outfmt 5 -evalue 0.0000001 -word_size 16 -max_target_seqs 5 -dust no -num_threads "
		mega=mega + nthreads
		mega=mega + " -out "
		mega=mega + namefile
		mega=mega + ".premega.out"
		print mega
		mega_cmd=commands.getstatusoutput(mega)
		print mega_cmd

		# Run Blastxml
		xml=Java + " -classpath "
		xml=xml + PathSeq_java
		xml=xml + " blastxml "
		xml=xml + namefile
		xml=xml + ".premega.out "
		xml=xml + namefile
		xml=xml + ".hit"
		print xml
		xml_cmd=commands.getstatusoutput(xml)
		print xml_cmd

		# create full query from the original reads and update Hit table
		exqfull=Java + " -classpath "
		exqfull=exqfull + PathSeq_java
		exqfull=exqfull + " extractFullQuert4BHitTable " 
		exqfull=exqfull +  namefile
		exqfull=exqfull + " "
		exqfull=exqfull + namefile
		exqfull=exqfull + ".hit "
		exqfull=exqfull + namefile
		exqfull=exqfull + ".premega.hittable."
		exqfull=exqfull + str(id_step)
		exqfull=exqfull + "_"
		exqfull=exqfull + str(dbindex)
		print exqfull
		exqfull_cmd=commands.getstatusoutput(exqfull)
		print exqfull_cmd

		# annotate the Hittable
		annotate=Java + " -classpath "
		annotate=annotate + PathSeq_java
		annotate=annotate + " annotate_hittable " 
		annotate=annotate + PathSeq_java
		annotate=annotate + "/names.dmp "
		annotate=annotate + PathSeq_java
		annotate=annotate + "/nodes.dmp "
		annotate=annotate + namefile
		annotate=annotate + ".premega.hittable."
		annotate=annotate + str(id_step)
		annotate=annotate + "_"
		annotate=annotate + str(dbindex)
		annotate=annotate + " "
		annotate=annotate + namefile
		annotate=annotate + ".premega.annotate.hittable."
		annotate=annotate + str(id_step)
		annotate=annotate + "_"
		annotate=annotate + str(dbindex)
		print annotate
		annotate_cmd=commands.getstatusoutput(annotate)
		print annotate_cmd

		# Sorting the file"
		sort="sort +1 -2 -T " + Tmp_dir
		sort=sort + " "
		sort=sort + namefile
		sort=sort + ".premega.annotate.hittable."
		sort=sort + str(id_step)
		sort=sort + "_"
		sort=sort + str(dbindex)
		sort=sort + " > "
		sort=sort + namefile
		sort=sort + ".premega.sort.tmp."
		sort=sort + str(id_step)
		sort=sort + "_"
		sort=sort + str(dbindex)
		print sort
		sort_cmd=commands.getstatusoutput(sort)
		print sort_cmd

		# Extract unmapped reads from the Hit table
		unmap=Java + " -classpath "
		unmap=unmap + PathSeq_java
		unmap=unmap + " extractUnmapped_Adapterblast " 
		unmap=unmap + namefile
		unmap=unmap + ".premega.sort.tmp."
		unmap=unmap + str(id_step)		
		unmap=unmap + "_"		
		unmap=unmap + str(dbindex)		
		unmap=unmap + premega_thres
		unmap=unmap + namefile
		unmap=unmap + " "
		unmap=unmap + namefile
		unmap=unmap + ".unmappedpremega.fq1."
		unmap=unmap + str(id_step)		
		unmap=unmap + "_"		
		unmap=unmap + str(dbindex)
		unmap=unmap + " "
		unmap=unmap + namefile
		unmap=unmap + ".mappedpremega.fq1."
		unmap=unmap + str(id_step)		
		unmap=unmap + "_"		
		unmap=unmap + str(dbindex)
		print unmap
		unmap_cmd=commands.getstatusoutput(unmap)
		print unmap_cmd

		# Copy the unmapped reads to original file for running next round of database
		copy="cp "+ namefile
		copy=copy + ".unmappedpremega.fq1."
		copy=copy + str(id_step)
		copy=copy + "_"
		copy=copy + str(dbindex)
		copy=copy + " " 
		copy=copy + namefile
		print copy
		copy_cmd=commands.getstatusoutput(copy)
		print copy_cmd

		print "Statistics after PREMEGABLAST Step"
		stat="wc -l " + namefile
		stat=stat + " > "
		stat=stat + namefile
		stat=stat + ".premega."						
		stat=stat + str(id_step)
		stat=stat + "_"
		stat=stat + str(dbindex)
		stat=stat + ".stat"
		print stat
		stat_cmd=commands.getstatusoutput(stat)
		print stat_cmd

	elif data_split[0] == "BLASTX":
		print "BLASTX";				
		# Convert the FQ1 to Fasta
		fq1_2_fasta = Java + " -classpath "
		fq1_2_fasta = fq1_2_fasta + PathSeq_java
		fq1_2_fasta = fq1_2_fasta + " FQone2Fasta " 
		fq1_2_fasta = fq1_2_fasta + namefile
		fq1_2_fasta = fq1_2_fasta + " "
		fq1_2_fasta = fq1_2_fasta + namefile
		fq1_2_fasta = fq1_2_fasta + ".fasta"
		print fq1_2_fasta
		fq1_2_fasta_cmd=commands.getstatusoutput(fq1_2_fasta)
		print fq1_2_fasta_cmd


		blastx=Blast_loc + "blastx -query " 
		blastx=blastx + namefile
		blastx=blastx + ".fasta -db \""
		blastx=blastx + data_split[1]
		blastx=blastx + "\" -outfmt 5 -evalue 1 -gapopen 11 -gapextend 1 -max_target_seqs 5 -num_threads "
		blastx=blastx + nthreads
		blastx=blastx + " -out "
		blastx=blastx + namefile
		blastx=blastx + ".blastx.out"
		print blastx
		blastx_cmd=commands.getstatusoutput(blastx)
		print blastx_cmd

		# Run Blastxml
		xml=Java + " -classpath "
		xml=xml + PathSeq_java
		xml=xml + " blastxml "
		xml=xml + namefile
		xml=xml + ".blastx.out "
		xml=xml + namefile
		xml=xml + ".hit"
		print xml
		xml_cmd=commands.getstatusoutput(xml)
		print xml_cmd

		# create full query from the original reads and update Hit table
		exqfull=Java + " -classpath "
		exqfull=exqfull + PathSeq_java
		exqfull=exqfull + " extractFullQuert4BHitTable " 
		exqfull=exqfull +  namefile
		exqfull=exqfull + " "
		exqfull=exqfull + namefile
		exqfull=exqfull + ".hit "
		exqfull=exqfull + namefile
		exqfull=exqfull + ".blastx.hittable."
		exqfull=exqfull + str(id_step)
		exqfull=exqfull + "_"
		exqfull=exqfull + str(dbindex)
		print exqfull
		exqfull_cmd=commands.getstatusoutput(exqfull)
		print exqfull_cmd

		# annotate the Hittable
		annotate=Java+ " -classpath "
		annotate=annotate + PathSeq_java
		annotate=annotate + " annotate_hittable " 
		annotate=annotate + PathSeq_java
		annotate=annotate + "/names.dmp "
		annotate=annotate + PathSeq_java
		annotate=annotate + "/nodes.dmp "
		annotate=annotate + namefile
		annotate=annotate + ".blastx.hittable."
		annotate=annotate + str(id_step)
		annotate=annotate + "_"
		annotate=annotate + str(dbindex)
		annotate=annotate + " "
		annotate=annotate + namefile
		annotate=annotate + ".blastx.annotate.hittable."
		annotate=annotate + str(id_step)
		annotate=annotate + "_"
		annotate=annotate + str(dbindex)
		print annotate
		annotate_cmd=commands.getstatusoutput(annotate)
		print annotate_cmd

		# Sorting the file"
		sort="sort +1 -2 -T " + Tmp_dir
		sort=sort + " "
		sort=sort + namefile
		sort=sort + ".blastx.annotate.hittable."
		sort=sort + str(id_step)
		sort=sort + "_"
		sort=sort + str(dbindex)
		sort=sort + " > "
		sort=sort + namefile
		sort=sort + ".blastx.sort.tmp."
		sort=sort + str(id_step)
		sort=sort + "_"
		sort=sort + str(dbindex)
		print sort
		sort_cmd=commands.getstatusoutput(sort)
		print sort_cmd

		# Extract unmapped reads from the Hit table
		unmap=Java + " -classpath "
		unmap=unmap + PathSeq_java
		unmap=unmap + " extractUnmapped_newlatest " 
		unmap=unmap + namefile
		unmap=unmap + ".blastx.sort.tmp."
		unmap=unmap + str(id_step)
		unmap=unmap + "_"
		unmap=unmap + str(dbindex)		
		unmap=unmap + blastx_thres
		unmap=unmap + namefile
		unmap=unmap + " "
		unmap=unmap + namefile
		unmap=unmap + ".unmappedblastx.fq1."
		unmap=unmap + str(id_step)
		unmap=unmap + "_"
		unmap=unmap + str(dbindex)
		unmap=unmap + " "
		unmap=unmap + namefile
		unmap=unmap + ".mappedblastx.fq1."
		unmap=unmap + str(id_step)
		unmap=unmap + "_"
		unmap=unmap + str(dbindex)
		print unmap
		unmap_cmd=commands.getstatusoutput(unmap)
		print unmap_cmd


		# Copy the unmapped reads to original file for running next round of database
		copy="cp " + namefile
		copy=copy + ".unmappedblastx.fq1."
		copy=copy + str(id_step)
		copy=copy + "_"
		copy=copy + str(dbindex)
		copy=copy + " " 
		copy=copy + namefile
		print copy
		copy_cmd=commands.getstatusoutput(copy)
		print copy_cmd							

		print "Statistics after BLASTX Step"
		stat="wc -l " + namefile
		stat=stat + " > "
		stat=stat + namefile
		stat=stat + ".blastx."							
		stat=stat + str(id_step)
		stat=stat + "_"
		stat=stat + str(dbindex)
		stat=stat + ".stat"
		print stat
		stat_cmd=commands.getstatusoutput(stat)
		print stat_cmd
	elif data_split[0] == "TBLASTX":
		print "TBLASTX";				
		# Convert the FQ1 to Fasta
		fq1_2_fasta = Java + " -classpath "
		fq1_2_fasta = fq1_2_fasta + PathSeq_java
		fq1_2_fasta = fq1_2_fasta + " FQone2Fasta " 
		fq1_2_fasta = fq1_2_fasta + namefile
		fq1_2_fasta = fq1_2_fasta + " "
		fq1_2_fasta = fq1_2_fasta + namefile
		fq1_2_fasta = fq1_2_fasta + ".fasta"
		print fq1_2_fasta
		fq1_2_fasta_cmd=commands.getstatusoutput(fq1_2_fasta)
		print fq1_2_fasta_cmd


		tblastx=Blast_loc + "tblastx -query " 
		tblastx=tblastx + namefile
		tblastx=tblastx + ".fasta -db \""
		tblastx=tblastx + data_split[1]
		tblastx=tblastx + "\" -outfmt 5 -evalue 1 -max_target_seqs 5 -num_threads "
		tblastx=tblastx + nthreads
		tblastx=tblastx + " -out "
		tblastx=tblastx + namefile
		tblastx=tblastx + ".tblastx.out"
		print tblastx
		tblastx_cmd=commands.getstatusoutput(tblastx)
		print tblastx_cmd

		# Run Blastxml
		xml=Java + " -classpath "
		xml=xml + PathSeq_java
		xml=xml + " blastxml "
		xml=xml + namefile
		xml=xml + ".tblastx.out "
		xml=xml + namefile
		xml=xml + ".hit"
		print xml
		xml_cmd=commands.getstatusoutput(xml)
		print xml_cmd

		# create full query from the original reads and update Hit table
		exqfull=Java + " -classpath "
		exqfull=exqfull + PathSeq_java
		exqfull=exqfull + " extractFullQuert4BHitTable " 
		exqfull=exqfull +  namefile
		exqfull=exqfull + " "
		exqfull=exqfull + namefile
		exqfull=exqfull + ".hit "
		exqfull=exqfull + namefile
		exqfull=exqfull + ".tblastx.hittable."
		exqfull=exqfull + str(id_step)
		exqfull=exqfull + "_"
		exqfull=exqfull + str(dbindex)
		print exqfull
		exqfull_cmd=commands.getstatusoutput(exqfull)
		print exqfull_cmd

		# annotate the Hittable
		annotate=Java+ " -classpath "
		annotate=annotate + PathSeq_java
		annotate=annotate + " annotate_hittable " 
		annotate=annotate + PathSeq_java
		annotate=annotate + "/names.dmp "
		annotate=annotate + PathSeq_java
		annotate=annotate + "/nodes.dmp "
		annotate=annotate + namefile
		annotate=annotate + ".tblastx.hittable."
		annotate=annotate + str(id_step)
		annotate=annotate + "_"
		annotate=annotate + str(dbindex)
		annotate=annotate + " "
		annotate=annotate + namefile
		annotate=annotate + ".tblastx.annotate.hittable."
		annotate=annotate + str(id_step)
		annotate=annotate + "_"
		annotate=annotate + str(dbindex)
		print annotate
		annotate_cmd=commands.getstatusoutput(annotate)
		print annotate_cmd

		# Sorting the file"
		sort="sort +1 -2 -T " + Tmp_dir
		sort=sort + " "
		sort=sort + namefile
		sort=sort + ".tblastx.annotate.hittable."
		sort=sort + str(id_step)
		sort=sort + "_"
		sort=sort + str(dbindex)
		sort=sort + " > "
		sort=sort + namefile
		sort=sort + ".tblastx.sort.tmp."
		sort=sort + str(id_step)
		sort=sort + "_"
		sort=sort + str(dbindex)
		print sort
		sort_cmd=commands.getstatusoutput(sort)
		print sort_cmd

		# Extract unmapped reads from the Hit table
		unmap=Java + " -classpath "
		unmap=unmap + PathSeq_java
		unmap=unmap + " extractUnmapped_newlatest " 
		unmap=unmap + namefile
		unmap=unmap + ".tblastx.sort.tmp."
		unmap=unmap + str(id_step)
		unmap=unmap + "_"
		unmap=unmap + str(dbindex)		
		unmap=unmap + blastx_thres
		unmap=unmap + namefile
		unmap=unmap + " "
		unmap=unmap + namefile
		unmap=unmap + ".unmappedtblastx.fq1."
		unmap=unmap + str(id_step)
		unmap=unmap + "_"
		unmap=unmap + str(dbindex)
		unmap=unmap + " "
		unmap=unmap + namefile
		unmap=unmap + ".mappedtblastx.fq1."
		unmap=unmap + str(id_step)
		unmap=unmap + "_"
		unmap=unmap + str(dbindex)
		print unmap
		unmap_cmd=commands.getstatusoutput(unmap)
		print unmap_cmd


		# Copy the unmapped reads to original file for running next round of database
		copy="cp " + namefile
		copy=copy + ".unmappedtblastx.fq1."
		copy=copy + str(id_step)
		copy=copy + "_"
		copy=copy + str(dbindex)
		copy=copy + " " 
		copy=copy + namefile
		print copy
		copy_cmd=commands.getstatusoutput(copy)
		print copy_cmd							

		print "Statistics after TBLASTX Step"
		stat="wc -l " + namefile
		stat=stat + " > "
		stat=stat + namefile
		stat=stat + ".tblastx."							
		stat=stat + str(id_step)
		stat=stat + "_"
		stat=stat + str(dbindex)
		stat=stat + ".stat"
		print stat
		stat_cmd=commands.getstatusoutput(stat)
		print stat_cmd
	elif data_split[0] == "TBLASTN":
		print "TBLASTN";				
		# Convert the FQ1 to Fasta
		fq1_2_fasta = Java + " -classpath "
		fq1_2_fasta = fq1_2_fasta + PathSeq_java
		fq1_2_fasta = fq1_2_fasta + " FQone2Fasta " 
		fq1_2_fasta = fq1_2_fasta + namefile
		fq1_2_fasta = fq1_2_fasta + " "
		fq1_2_fasta = fq1_2_fasta + namefile
		fq1_2_fasta = fq1_2_fasta + ".fasta"
		print fq1_2_fasta
		fq1_2_fasta_cmd=commands.getstatusoutput(fq1_2_fasta)
		print fq1_2_fasta_cmd


		tblastn=Blast_loc + "tblastn -query " 
		tblastn=tblastn + namefile
		tblastn=tblastn + ".fasta -db \""
		tblastn=tblastn + data_split[1]
		tblastn=tblastn + "\" -outfmt 5 -evalue 1 -max_target_seqs 5 -num_threads "
		tblastn=tblastn + nthreads
		tblastn=tblastn + " -out "
		tblastn=tblastn + namefile
		tblastn=tblastn + ".tblastn.out"
		print tblastn
		tblastn_cmd=commands.getstatusoutput(tblastx)
		print tblastn_cmd

		# Run Blastxml
		xml=Java + " -classpath "
		xml=xml + PathSeq_java
		xml=xml + " blastxml "
		xml=xml + namefile
		xml=xml + ".tblastn.out "
		xml=xml + namefile
		xml=xml + ".hit"
		print xml
		xml_cmd=commands.getstatusoutput(xml)
		print xml_cmd

		# create full query from the original reads and update Hit table
		exqfull=Java + " -classpath "
		exqfull=exqfull + PathSeq_java
		exqfull=exqfull + " extractFullQuert4BHitTable " 
		exqfull=exqfull +  namefile
		exqfull=exqfull + " "
		exqfull=exqfull + namefile
		exqfull=exqfull + ".hit "
		exqfull=exqfull + namefile
		exqfull=exqfull + ".tblastn.hittable."
		exqfull=exqfull + str(id_step)
		exqfull=exqfull + "_"
		exqfull=exqfull + str(dbindex)
		print exqfull
		exqfull_cmd=commands.getstatusoutput(exqfull)
		print exqfull_cmd

		# annotate the Hittable
		annotate=Java+ " -classpath "
		annotate=annotate + PathSeq_java
		annotate=annotate + " annotate_hittable " 
		annotate=annotate + PathSeq_java
		annotate=annotate + "/names.dmp "
		annotate=annotate + PathSeq_java
		annotate=annotate + "/nodes.dmp "
		annotate=annotate + namefile
		annotate=annotate + ".tblastn.hittable."
		annotate=annotate + str(id_step)
		annotate=annotate + "_"
		annotate=annotate + str(dbindex)
		annotate=annotate + " "
		annotate=annotate + namefile
		annotate=annotate + ".tblastn.annotate.hittable."
		annotate=annotate + str(id_step)
		annotate=annotate + "_"
		annotate=annotate + str(dbindex)
		print annotate
		annotate_cmd=commands.getstatusoutput(annotate)
		print annotate_cmd

		# Sorting the file"
		sort="sort +1 -2 -T " + Tmp_dir
		sort=sort + " "
		sort=sort + namefile
		sort=sort + ".tblastn.annotate.hittable."
		sort=sort + str(id_step)
		sort=sort + "_"
		sort=sort + str(dbindex)
		sort=sort + " > "
		sort=sort + namefile
		sort=sort + ".tblastn.sort.tmp."
		sort=sort + str(id_step)
		sort=sort + "_"
		sort=sort + str(dbindex)
		print sort
		sort_cmd=commands.getstatusoutput(sort)
		print sort_cmd

		# Extract unmapped reads from the Hit table
		unmap=Java + " -classpath "
		unmap=unmap + PathSeq_java
		unmap=unmap + " extractUnmapped_newlatest " 
		unmap=unmap + namefile
		unmap=unmap + ".tblastn.sort.tmp."
		unmap=unmap + str(id_step)
		unmap=unmap + "_"
		unmap=unmap + str(dbindex)		
		unmap=unmap + blastx_thres
		unmap=unmap + namefile
		unmap=unmap + " "
		unmap=unmap + namefile
		unmap=unmap + ".unmappedtblastn.fq1."
		unmap=unmap + str(id_step)
		unmap=unmap + "_"
		unmap=unmap + str(dbindex)
		unmap=unmap + " "
		unmap=unmap + namefile
		unmap=unmap + ".mappedtblastn.fq1."
		unmap=unmap + str(id_step)
		unmap=unmap + "_"
		unmap=unmap + str(dbindex)
		print unmap
		unmap_cmd=commands.getstatusoutput(unmap)
		print unmap_cmd


		# Copy the unmapped reads to original file for running next round of database
		copy="cp " + namefile
		copy=copy + ".unmappedtblastn.fq1."
		copy=copy + str(id_step)
		copy=copy + "_"
		copy=copy + str(dbindex)
		copy=copy + " " 
		copy=copy + namefile
		print copy
		copy_cmd=commands.getstatusoutput(copy)
		print copy_cmd							

		print "Statistics after TBLASTN Step"
		stat="wc -l " + namefile
		stat=stat + " > "
		stat=stat + namefile
		stat=stat + ".tblastn."							
		stat=stat + str(id_step)
		stat=stat + "_"
		stat=stat + str(dbindex)
		stat=stat + ".stat"
		print stat
		stat_cmd=commands.getstatusoutput(stat)
		print stat_cmd
	elif data_split[0] == "FINISH":
		print "FINISH"
		b_file=namefile + ".finaloutput"
		finaloutname=open(b_file,'w')
		finaloutname.write("Completed the mapping on the reads")
		finaloutname.close()

		if compute == "STANDALONE" :
			count_finish = "ls -l " + cdir
			count_finish = count_finish + "/"
			count_finish = count_finish + "*.finaloutput | "
			count_finish = count_finish + "wc -l"
			print count_finish
			count_finish_cmd=commands.getstatusoutput(count_finish)
			print count_finish_cmd
		else:
			count_finish = "ls -l " + cdir
			count_finish = count_finish + "/"
			count_finish = count_finish + full_file
			count_finish = count_finish + "_*_spt/"
			count_finish = count_finish + "*.finaloutput | "
			count_finish = count_finish + "wc -l"
			print count_finish
			count_finish_cmd=commands.getstatusoutput(count_finish)
			print count_finish_cmd

		print count_finish_cmd[1]


		if count_finish_cmd[1] == total_split:

			# Convert the FQ1 to Fastq
			run_concate = "python" + " "
			run_concate = run_concate + PathSeq_loc

			if compute == "STANDALONE" :
				run_concate = run_concate + "/concat_files_standalone.py "
			else:
				run_concate = run_concate + "/concat_files.py "

			run_concate = run_concate + namefile
			run_concate = run_concate + " "
			run_concate = run_concate + configfile
			run_concate = run_concate + " "
			run_concate = run_concate + pdir
			run_concate = run_concate + " "
			run_concate = run_concate + cdir
			run_concate = run_concate + " "
			run_concate = run_concate + id_step
			run_concate = run_concate + " "
			run_concate = run_concate + namefile_o
			run_concate = run_concate + " "
			run_concate = run_concate + mergesamjar
			run_concate = run_concate + " "
			run_concate = run_concate + Java
			run_concate = run_concate + " "
			run_concate = run_concate + Tmp_dir
			run_concate = run_concate + " "
			run_concate = run_concate + Samtools

			print run_concate
			run_concate_cmd=commands.getstatusoutput(run_concate)
			print run_concate_cmd								

			mkdir_file = "mkdir " +pdir 
			mkdir_file = mkdir_file + "/"
			mkdir_file = mkdir_file + "Final_combine_results"
			print mkdir_file
			mkdir_file_cmd=commands.getstatusoutput(mkdir_file)
			print mkdir_file_cmd

			cp_file = "rsync -av " + pdir 
			cp_file = cp_file + "/"
			cp_file = cp_file + "*_PathSeq/combine_results/ "
			cp_file = cp_file + pdir
			cp_file = cp_file + "/"
			cp_file = cp_file + "Final_combine_results/"
			print cp_file
			cp_file_cmd=commands.getstatusoutput(cp_file)
			print cp_file_cmd																	
			dir_results = pdir + "/"
			dir_results = dir_results + "Final_combine_results"
			print dir_results
			os.chdir(dir_results)
			
			dir_results = "ls -l "
			print dir_results
			dir_results_cmd=commands.getstatusoutput(dir_results)
			print dir_results_cmd
			

			htmlreport = Java + " -classpath "
			htmlreport = htmlreport + PathSeq_java 
			htmlreport = htmlreport + " HTML_Report " 
			htmlreport = htmlreport + O_config 
			htmlreport = htmlreport + " " 
			htmlreport = htmlreport + O_inputfile
			htmlreport = htmlreport + " " 
			htmlreport = htmlreport + pdir
			htmlreport = htmlreport + "/Final_combine_results/REPORT.html" 
			print htmlreport
			htmlreport_cmd=commands.getstatusoutput(htmlreport)
			print htmlreport_cmd
			

	elif data_split[0] == "FINISH_CLEAN":
		print "FINISH CLEAN"
		b_file=namefile + ".finaloutput"
		finaloutname=open(b_file,'w')
		finaloutname.write("Completed the mapping on the reads")
		finaloutname.close()

		if compute == "STANDALONE" :
			count_finish = "ls -l " + cdir
			count_finish = count_finish + "/"
			count_finish = count_finish + "*.finaloutput | "
			count_finish = count_finish + "wc -l"
			print count_finish
			count_finish_cmd=commands.getstatusoutput(count_finish)
			print count_finish_cmd
		else:
			count_finish = "ls -l " + cdir
			count_finish = count_finish + "/"
			count_finish = count_finish + full_file
			count_finish = count_finish + "_*_spt/"
			count_finish = count_finish + "*.finaloutput | "
			count_finish = count_finish + "wc -l"
			print count_finish
			count_finish_cmd=commands.getstatusoutput(count_finish)
			print count_finish_cmd

		print count_finish_cmd[1]


		if count_finish_cmd[1] == total_split:

			# Convert the FQ1 to Fastq
			run_concate = "python" + " "
			run_concate = run_concate + PathSeq_loc

			if compute == "STANDALONE" :
				run_concate = run_concate + "/concat_files_standalone.py "
			else:
				run_concate = run_concate + "/concat_files.py "

			run_concate = run_concate + namefile
			run_concate = run_concate + " "
			run_concate = run_concate + configfile
			run_concate = run_concate + " "
			run_concate = run_concate + pdir
			run_concate = run_concate + " "
			run_concate = run_concate + cdir
			run_concate = run_concate + " "
			run_concate = run_concate + id_step
			run_concate = run_concate + " "
			run_concate = run_concate + namefile_o
			run_concate = run_concate + " "
			run_concate = run_concate + mergesamjar
			run_concate = run_concate + " "
			run_concate = run_concate + Java
			run_concate = run_concate + " "
			run_concate = run_concate + Tmp_dir
			run_concate = run_concate + " "
			run_concate = run_concate + Samtools

			print run_concate


			run_concate_cmd=commands.getstatusoutput(run_concate)
			print run_concate_cmd								

			mkdir_file = "mkdir " +pdir 
			mkdir_file = mkdir_file + "/"
			mkdir_file = mkdir_file + "Final_combine_results"
			print mkdir_file
			mkdir_file_cmd=commands.getstatusoutput(mkdir_file)
			print mkdir_file_cmd

			cp_file = "rsync -av " + pdir 
			cp_file = cp_file + "/"
			cp_file = cp_file + "*_PathSeq/combine_results/ "
			cp_file = cp_file + pdir
			cp_file = cp_file + "/"
			cp_file = cp_file + "Final_combine_results/"
			print cp_file
			cp_file_cmd=commands.getstatusoutput(cp_file)
			print cp_file_cmd																	
			dir_results = pdir + "/"
			dir_results = dir_results + "Final_combine_results"
			print dir_results
			os.chdir(dir_results)
			
			dir_results = "ls -l "
			print dir_results
			dir_results_cmd=commands.getstatusoutput(dir_results)
			print dir_results_cmd
			

			htmlreport = Java + " -classpath "
			htmlreport = htmlreport + PathSeq_java 
			htmlreport = htmlreport + " HTML_Report " 
			htmlreport = htmlreport + O_config 
			htmlreport = htmlreport + " " 
			htmlreport = htmlreport + O_inputfile
			htmlreport = htmlreport + " " 
			htmlreport = htmlreport + pdir
			htmlreport = htmlreport + "/Final_combine_results/REPORT.html" 
			print htmlreport
			htmlreport_cmd=commands.getstatusoutput(htmlreport)
			print htmlreport_cmd
			
			dir_results = pdir + "/"
			print dir_results
			os.chdir(dir_results)
			
			dir_results = "ls -l "
			print dir_results
			dir_results_cmd=commands.getstatusoutput(dir_results)
			print dir_results_cmd

			rmfiles=pdir + "/clean.files"	
			finalout=open(rmfiles,'w')
			
			pathseq_out=pdir + "/"
			pathseq_out=pathseq_out + "*_PathSeq"
			print pathseq_out
			filelist = glob.glob(pathseq_out)
			for path in filelist:
				print path
				rmd = Java + " -classpath "
				rmd = rmd + PathSeq_java 
				rmd = rmd + " DeleteFolders " 
				rmd = rmd + "DIR " 
				rmd = rmd + path
				finalout.write(rmd)
				finalout.write("\n")
				#rmd_cmd=commands.getstatusoutput(rmd)
				#print rmd_cmd
			#	shutil.rmtree(path, ignore_errors=True)
			
			pathseq_out=pdir + "/"
			pathseq_out=pathseq_out + "*.config"
			filelist = glob.glob(pathseq_out)
			for path in filelist:
				print path
				rmd = Java + " -classpath "
				rmd = rmd + PathSeq_java 
				rmd = rmd + " DeleteFolders " 
				rmd = rmd + "FILE " 
				rmd = rmd + path
				finalout.write(rmd)
				finalout.write("\n")
				print rmd
				#rmd_cmd=commands.getstatusoutput(rmd)
				#print rmd_cmd
			#	os.remove(path)
			
			
			pathseq_out=pdir + "/"
			pathseq_out=pathseq_out + "*.config.current.*"
			print pathseq_out
			filelist = glob.glob(pathseq_out)
			for path in filelist:
				print path
				rmd = Java + " -classpath "
				rmd = rmd + PathSeq_java 
				rmd = rmd + " DeleteFolders " 
				rmd = rmd + "FILE " 
				rmd = rmd + path
				finalout.write(rmd)
				finalout.write("\n")
				print rmd
				#rmd_cmd=commands.getstatusoutput(rmd)
				#print rmd_cmd
		#		os.remove(path)
			
			pathseq_out=pdir + "/"
			pathseq_out=pathseq_out + "*.configlst"
			print pathseq_out
			filelist = glob.glob(pathseq_out)
			for path in filelist:
				print path
				rmd = Java + " -classpath "
				rmd = rmd + PathSeq_java 
				rmd = rmd + " DeleteFolders " 
				rmd = rmd + "FILE " 
				rmd = rmd + path
				finalout.write(rmd)
				finalout.write("\n")
				print rmd
				#rmd_cmd=commands.getstatusoutput(rmd)
				#print rmd_cmd
			#	os.remove(path)
			
			pathseq_out=pdir + "/"
			pathseq_out=pathseq_out + "*.command"
			filelist = glob.glob(pathseq_out)
			for path in filelist:
				print path
				rmd = Java + " -classpath "
				rmd = rmd + PathSeq_java 
				rmd = rmd + " DeleteFolders " 
				rmd = rmd + "FILE " 
				rmd = rmd + path
				finalout.write(rmd)
				finalout.write("\n")
				print rmd
				#rmd_cmd=commands.getstatusoutput(rmd)
				#print rmd_cmd
			#	os.remove(path)
			
			pathseq_out=pdir + "/"
			pathseq_out=pathseq_out + "*.loader"
			print pathseq_out
			filelist = glob.glob(pathseq_out)
			for path in filelist:
				print path
				rmd = Java + " -classpath "
				rmd = rmd + PathSeq_java 
				rmd = rmd + " DeleteFolders " 
				rmd = rmd + "FILE " 
				rmd = rmd + path
				print rmd
				finalout.write(rmd)
				finalout.write("\n")
				#rmd_cmd=commands.getstatusoutput(rmd)
				#print rmd_cmd
			#	os.remove(path)
			
			pathseq_out=pdir + "/"
			pathseq_out=pathseq_out + "*.current"
			print pathseq_out
			filelist = glob.glob(pathseq_out)
			for path in filelist:
				print path
				rmd = Java + " -classpath "
				rmd = rmd + PathSeq_java 
				rmd = rmd + " DeleteFolders " 
				rmd = rmd + "FILE " 
				rmd = rmd + path
				print rmd
				finalout.write(rmd)
				finalout.write("\n")
				#rmd_cmd=commands.getstatusoutput(rmd)
				#print rmd_cmd
			#	os.remove(path)
			pathseq_out=pdir + "/"
			pathseq_out=pathseq_out + "*.current.*"
			print pathseq_out
			filelist = glob.glob(pathseq_out)
			for path in filelist:
				print path
				rmd = Java + " -classpath "
				rmd = rmd + PathSeq_java 
				rmd = rmd + " DeleteFolders " 
				rmd = rmd + "FILE " 
				rmd = rmd + path
				print rmd
				finalout.write(rmd)
				finalout.write("\n")
				#rmd_cmd=commands.getstatusoutput(rmd)
				#print rmd_cmd
			#	os.remove(path)
			
			finalout.close()

	elif data_split[0] == "GATHER": # Gather steps that gathers the analyzed data
		print "Completed the mapping on the reads"
		b_file=namefile + ".finaloutput"
		finaloutname=open(b_file,'w')
		finaloutname.write("Completed the mapping on the reads")
		finaloutname.close()

		if compute == "STANDALONE" :
			count_finish = "ls -l " + cdir
			count_finish = count_finish + "/"
			count_finish = count_finish + "*.finaloutput | "
			count_finish = count_finish + "wc -l"
			print count_finish
			count_finish_cmd=commands.getstatusoutput(count_finish)
			print count_finish_cmd

		else:
			count_finish = "ls -l " + cdir
			count_finish = count_finish + "/"
			count_finish = count_finish + full_file
			count_finish = count_finish + "_*_spt/"
			count_finish = count_finish + "*.finaloutput | "
			count_finish = count_finish + "wc -l"
			print count_finish
			count_finish_cmd=commands.getstatusoutput(count_finish)
			print count_finish_cmd

		print count_finish_cmd[1]

		if count_finish_cmd[1] == total_split:
			print "Completed the mapping on the reads"
			b_file=cdir + "/completed.txt"
			
			if os.path.exists(b_file) :
				print "Already initiated runs"
			else:
				finaloutname=open(b_file,'w')
				finaloutname.write("C")
				finaloutname.close()
			
				# Convert the FQ1 to Fastq
				run_concate = "python" + " "
				run_concate = run_concate + PathSeq_loc

				if compute == "STANDALONE" :
					run_concate = run_concate + "/concat_files_standalone.py "
				else:
					run_concate = run_concate + "/concat_files.py "

				run_concate = run_concate + namefile
				run_concate = run_concate + " "
				run_concate = run_concate + configfile
				run_concate = run_concate + " "
				run_concate = run_concate + pdir
				run_concate = run_concate + " "
				run_concate = run_concate + cdir
				run_concate = run_concate + " "
				run_concate = run_concate + id_step
				run_concate = run_concate + " "
				run_concate = run_concate + namefile_o
				run_concate = run_concate + " "
				run_concate = run_concate + mergesamjar
				run_concate = run_concate + " "
				run_concate = run_concate + Java
				run_concate = run_concate + " "
				run_concate = run_concate + Tmp_dir
				run_concate = run_concate + " "
				run_concate = run_concate + Samtools

				print run_concate
				run_concate_cmd=commands.getstatusoutput(run_concate)
				print run_concate_cmd		

				new_id_step = int(id_step) + 1
				cconfig = ""									
				newconfiglist = cdir + "/"
				newconfiglist = newconfiglist + "next.configlist"
				newconfiglist = newconfiglist + str(new_id_step)


				foutname = open(nextconfiglist, 'r')
				data_line=foutname.readlines()
				foutname.close()


				index=0
				nfoutname = open(newconfiglist, 'w')
				for no_databases2 in data_line:
					line_1=no_databases2.strip()
					if index == 0:
						cconfig = line_1
						index = index + 1
					else:
						nfoutname.write(line_1);
						nfoutname.write("\n")

				nfoutname.close()

				dir_results = cdir + "/"
				dir_results = dir_results + "combine_results"
				print dir_results
				os.chdir(dir_results)

				subjob = "python" + " "
				subjob = subjob + PathSeq_loc
				subjob = subjob + "/"
				subjob = subjob + "jobsubmission.py"
				subjob = subjob + " "
				subjob = subjob + namefile_o
				subjob = subjob + ".unmappedfinal.fq1"
				subjob = subjob + " "
				subjob = subjob + cconfig
				subjob = subjob + " "
				subjob = subjob + newconfiglist
				subjob = subjob + " "
				subjob = subjob + compute
				subjob = subjob + " "
				subjob = subjob + pdir
				subjob = subjob + " "
				subjob = subjob + str(new_id_step)
				subjob = subjob + " "
				subjob = subjob + Institute
				subjob = subjob + " "
				subjob = subjob + PathSeq_loc
				subjob = subjob + " "
				subjob = subjob + Tmp_dir
				subjob = subjob + " "
				subjob = subjob + Java
				subjob = subjob + " "
				subjob = subjob + Bwa_loc
				subjob = subjob + " "
				subjob = subjob + Blast_loc
				subjob = subjob + " "
				subjob = subjob + Repeatmasker_loc
				subjob = subjob + " "
				subjob = subjob + Python
				subjob = subjob + " "
				subjob = subjob + Package_loader
				subjob = subjob + " "
				subjob = subjob + Loader_file
				subjob = subjob + " "
				subjob = subjob + Assembler_loc
				subjob = subjob + " "
				subjob = subjob + O_config
				subjob = subjob + " "
				subjob = subjob + O_inputfile
				subjob = subjob + " "
				subjob = subjob + Samtools				
				print "&&&&&&&&&&&&&"
				print subjob
				print "&&&&&&&&&&&&&"
				subjob_cmd=commands.getstatusoutput(subjob)
				print subjob_cmd

	elif data_split[0] == "VELVET": # Velvet Assembler to run the assembly on unmapped reads
		print "VELVET"
		print cdir
		# Convert the FQ1 to Fastq
		fq1_2_fastq = Java + " -classpath "
		fq1_2_fastq = fq1_2_fastq + PathSeq_java 
		fq1_2_fastq = fq1_2_fastq + " FQone2Fastq " 
		fq1_2_fastq = fq1_2_fastq + namefile
		fq1_2_fastq = fq1_2_fastq + " "
		fq1_2_fastq = fq1_2_fastq + namefile
		fq1_2_fastq = fq1_2_fastq + ".fastq"
		print fq1_2_fastq
		fq1_2_fastq_cmd=commands.getstatusoutput(fq1_2_fastq)
		print fq1_2_fastq_cmd

		head_seq="head -1 "+namefile

		if data_split[1] == "SINGLEEND":
			# Convert the Velveth
			Assembler = Assembler_loc + "velveth "
			Assembler = Assembler + cdir
			Assembler = Assembler +"/velvet_output/ " 
			Assembler = Assembler + hash_length
			Assembler = Assembler + " "
			Assembler = Assembler + " -fastq -short "
			Assembler = Assembler + namefile
			Assembler = Assembler + ".fastq"
			print Assembler
			Assembler_cmd=commands.getstatusoutput(Assembler)
			print Assembler_cmd

			# Convert the Velvetg
			Assembler = Assembler_loc + "velvetg "
			Assembler = Assembler + cdir
			Assembler = Assembler + "/velvet_output/ "
			Assembler = Assembler + "-min_contig_lgth "
			Assembler = Assembler + minlength_contigs
			print Assembler
			Assembler_cmd=commands.getstatusoutput(Assembler)
			print Assembler_cmd
		elif data_split[1] == "PAIRED_END": # NOT ENABLED YET
			# Convert the Velveth
			Assembler = Assembler_loc + "velveth "
			Assembler = Assembler + cdir
			Assembler = Assembler +"/velvet_output/ " 
			Assembler = Assembler + hash_length
			Assembler = Assembler + " "
			Assembler = Assembler + " -fastq -shortPaired "
			Assembler = Assembler + namefile
			Assembler = Assembler + ".fastq"
			print Assembler
			Assembler_cmd=commands.getstatusoutput(Assembler)
			print Assembler_cmd

			# Convert the Velveth
			Assembler = Assembler_loc + "velvetg "
			Assembler = Assembler + cdir
			Assembler = Assembler + "/velvet_output/ "
			Assembler = Assembler + "-min_contig_lgth "
			Assembler = Assembler + data_split[2]
			print Assembler
			Assembler_cmd=commands.getstatusoutput(Assembler)
			print Assembler_cmd											

		# Copy the contigs file
		contig_fq1 = Java + " -classpath "
		contig_fq1 = contig_fq1 + PathSeq_java
		contig_fq1 = contig_fq1 + " Fas2FQ1 "
		contig_fq1 = contig_fq1 + cdir
		contig_fq1 = contig_fq1 + "/velvet_output/contigs.fa " 
		contig_fq1 = contig_fq1 + cdir
		contig_fq1 = contig_fq1 + "/"
		contig_fq1 = contig_fq1 + namefile_o
		contig_fq1 = contig_fq1 + ".contigs.fq1"
		print contig_fq1
		contigfq1_cmd=commands.getstatusoutput(contig_fq1)
		print contigfq1_cmd
	elif data_split[0] == "GATHERASSEMBLER":
		print "Completed the mapping on the reads"
		b_file=namefile + ".finaloutput"
		finaloutname=open(b_file,'w')
		finaloutname.write("Completed the mapping on the reads")
		finaloutname.close()

		print cdir
		print pdir

		mkdir_file = "mkdir " +cdir 
		mkdir_file = mkdir_file + "/"
		mkdir_file = mkdir_file + "combine_results"
		print mkdir_file
		mkdir_file_cmd=commands.getstatusoutput(mkdir_file)
		print mkdir_file_cmd

		cpfile="cp " +cdir
		cpfile=cpfile +"/"
		cpfile=cpfile +namefile_o
		cpfile=cpfile +".contigs.fq1 "
		cpfile=cpfile + cdir
		cpfile=cpfile + "/"
		cpfile=cpfile + "combine_results"
		print cpfile
		cpfile_cmd=commands.getstatusoutput(cpfile)
		print cpfile_cmd

		new_id_step = int(id_step) + 1
		cconfig = ""									
		newconfiglist = cdir + "/"
		newconfiglist = newconfiglist + "next.configlist"
		newconfiglist = newconfiglist + str(new_id_step)


		foutname = open(nextconfiglist, 'r')
		data_line=foutname.readlines()
		foutname.close()


		index=0
		nfoutname = open(newconfiglist, 'w')
		for no_databases2 in data_line:
			line_1=no_databases2.strip()
			if index == 0:
				cconfig = line_1
				index = index + 1
			else:
				nfoutname.write(line_1);
				nfoutname.write("\n")

		nfoutname.close()

		dir_results = cdir + "/"
		dir_results = dir_results + "combine_results"
		print dir_results
		os.chdir(dir_results)											

		subjob = "python" + " "
		subjob = subjob + PathSeq_loc
		subjob = subjob + "/"
		subjob = subjob + "jobsubmission.py"
		subjob = subjob + " "
		subjob = subjob + namefile_o
		subjob = subjob + ".contigs.fq1"
		subjob = subjob + " "
		subjob = subjob + cconfig
		subjob = subjob + " "
		subjob = subjob + newconfiglist
		subjob = subjob + " "
		subjob = subjob + compute
		subjob = subjob + " "
		subjob = subjob + pdir
		subjob = subjob + " "
		subjob = subjob + str(new_id_step)
		subjob = subjob + " "
		subjob = subjob + Institute
		subjob = subjob + " "
		subjob = subjob + PathSeq_loc
		subjob = subjob + " "
		subjob = subjob + Tmp_dir
		subjob = subjob + " "
		subjob = subjob + Java
		subjob = subjob + " "
		subjob = subjob + Bwa_loc
		subjob = subjob + " "
		subjob = subjob + Blast_loc
		subjob = subjob + " "
		subjob = subjob + Repeatmasker_loc
		subjob = subjob + " "
		subjob = subjob + Python
		subjob = subjob + " "
		subjob = subjob + Package_loader
		subjob = subjob + " "
		subjob = subjob + Loader_file
		subjob = subjob + " "
		subjob = subjob + Assembler_loc
		subjob = subjob + " "
		subjob = subjob + O_config
		subjob = subjob + " "
		subjob = subjob + O_inputfile
		subjob = subjob + " "
		subjob = subjob + Samtools		
		print "&&&&&&&&&&&&&"
		print subjob
		print "&&&&&&&&&&&&&"
		subjob_cmd=commands.getstatusoutput(subjob)
		print subjob_cmd
	elif data_split[0] == "CLEAN":
		clean_file = pdir + "/"
		clean_file = clean_file + "Clean.cmd"
		print clean_file
		clean_file_cmd=commands.getstatusoutput(clean_file)
		print clean_file_cmd

end_time = time.time()
timetaken= (end_time - start_time)
print "Time Taken:"
print timetaken
