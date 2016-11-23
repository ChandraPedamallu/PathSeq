#!/usr/bin/env python
# Created: Chandra Sekhar Pedamallu, DFCI
# Purpose: Job submission pipeline

# DFCI @ copyright

import sys
import os
import commands
import random
import time

start_time = time.time()

print "JOB SUBMISSION*********************"
#Arguments
args=sys.argv
print "Step 0: Read config, premegablast, megablast, and blastn config files"

namefile = args[1].strip() # Read in FQ1 format
configfile = args[2].strip()
nextconfiglist = args[3].strip()
compute = args[4].strip()
print compute
pdir=args[5].strip() # Parent Directory
id_step=args[6].strip() # ID the steps like separate config files generated

# Program Settings
Institute=args[7].strip()

# PathSeq installation or unzip location
PathSeq_loc=args[8].strip()
PathSeq_java=PathSeq_loc + "/Java"

# Temporary directory Location
Tmp_dir=args[9].strip()
# Java library Location
Java=args[10].strip()
# BWA Location
Bwa_loc=args[11].strip()
# BLAST Location
Blast_loc=args[12].strip()
# Repeatmasker Location
Repeatmasker_loc=args[13].strip()
# Python Location
Python=args[14].strip()
# Loader_package
Package_loader=args[15].strip()
# Loader Location
Loader_file=args[16].strip()
print Loader_file
# Assembler location
Assembler_loc=args[17].strip()
O_config=args[18].strip()
O_inputfile=args[19].strip()
Samtools=args[20].strip()
namefile_o = args[1].strip() # Read in FQ1 format

# TO BE CONFIGURED################

# Run the loader file
if Package_loader == "YES":
	print Loader_file
	loader_cmd=commands.getstatusoutput(Loader_file)
	print loader_cmd

ff = open(configfile, 'r')
database = ff.readlines()
ff.close()

print "Config File"
print configfile

cdir=pdir + "/"
cdir=cdir + id_step
cdir=cdir + "_PathSeq"
print cdir

# Make directory with 1st set
dir_mk = "mkdir " + cdir
print dir_mk
dir_mk_cmd=commands.getstatusoutput(dir_mk)
print dir_mk_cmd[1]

# Copying the input file into the directory generated as above
cp_file = "cp " + namefile
cp_file = cp_file + " "
cp_file = cp_file + cdir
print cp_file
cp_file_cmd=commands.getstatusoutput(cp_file)
print cp_file_cmd

# Change directory into the cdirectory <pdirectory>/"stepid_PathSeq
os.chdir(cdir)


c_config=configfile+ ".current."
c_config=c_config+ id_step
finaloutname=open(c_config,'w')


scatter_size=0
number_threads="1"
prefix_command=""
ff = open(configfile, 'r')
database = ff.readlines()
print database
for no_databases1 in database:
	line = no_databases1.strip()
	data_split=line.split(":")
	print data_split
	if data_split[0] == "SCATTER":
		scatter_size=data_split[1].strip()
		number_threads=data_split[2].strip()
		prefix_command=command=data_split[3].strip()
	else:
		finaloutname.write(line)
		finaloutname.write("\n")

finaloutname.close()
ff.close()

if compute == "STANDALONE":
	print "STANDALONE RUNS INITIATED"

	comand_line = "python" + " "
	comand_line = comand_line + PathSeq_loc
	comand_line = comand_line + "/steps.py "
	comand_line = comand_line + namefile
	comand_line = comand_line + " "
	comand_line = comand_line + configfile
	comand_line = comand_line + " "
	comand_line = comand_line + number_threads
	comand_line = comand_line + " "
	comand_line = comand_line + nextconfiglist
	comand_line = comand_line + " "
	comand_line = comand_line + pdir
	comand_line = comand_line + " "
	comand_line = comand_line + cdir
	comand_line = comand_line + " "
	comand_line = comand_line + namefile
	comand_line = comand_line + " 1"
	comand_line = comand_line + " "
	comand_line = comand_line + str(id_step)
	comand_line = comand_line + " "
	comand_line = comand_line + compute
	comand_line = comand_line + " "
	comand_line = comand_line + namefile_o
	comand_line = comand_line + " "
	comand_line = comand_line + Institute
	comand_line = comand_line + " "
	comand_line = comand_line + PathSeq_loc
	comand_line = comand_line + " "
	comand_line = comand_line + Tmp_dir
	comand_line = comand_line + " "
	comand_line = comand_line + Java
	comand_line = comand_line + " "
	comand_line = comand_line + Bwa_loc
	comand_line = comand_line + " "
	comand_line = comand_line + Blast_loc	
	comand_line = comand_line + " "
	comand_line = comand_line + Repeatmasker_loc
	comand_line = comand_line + " "
	comand_line = comand_line + Python	
	comand_line = comand_line + " "
	comand_line = comand_line + Package_loader
	comand_line = comand_line + " "
	comand_line = comand_line + Loader_file
	comand_line = comand_line + " "
	comand_line = comand_line + Assembler_loc
	comand_line = comand_line + " "
	comand_line = comand_line + O_config
	comand_line = comand_line + " "
	comand_line = comand_line + O_inputfile
	comand_line = comand_line + " "
	comand_line = comand_line + Samtools

	print comand_line
	comand_line_cmd=commands.getstatusoutput(comand_line)
	print comand_line_cmd

	clean_file=pdir + "/clean.files"
	if os.path.isfile(clean_file):
		chmod_line="chmod 777 "+ clean_file
		cline_chmod_line=commands.getstatusoutput(chmod_line)
		print cline_chmod_line
		cline_chmod_line=commands.getstatusoutput(clean_file)


	
else:
	print "CLUSTER SUBMISSION INITIATED"
	
	if scatter_size=="NA":
		countreads="wc -l < " + namefile
		print countreads
		countreads_cmd=commands.getstatusoutput(countreads)
		scatter_size=countreads_cmd[1]

	# SPLIT FILE INTO CHUNKS
	split_fq = Java + " -classpath " 
	split_fq = split_fq + PathSeq_java
	split_fq = split_fq + " FileSplitter " 
	split_fq = split_fq + namefile
	split_fq = split_fq + " "
	split_fq = split_fq + scatter_size
	split_fq = split_fq + " split.fq1 FOREACH"
	print split_fq
	split_fq_cmd=commands.getstatusoutput(split_fq)
	print split_fq_cmd[1]

	for x in range(0, int(str(split_fq_cmd[1]))):
		split_id = (x+1)
		job_id = Java + " -classpath " 
		job_id = job_id + PathSeq_java
		job_id = job_id + " jobidcreation 4 " 
		print job_id
		job_id_cmd=commands.getstatusoutput(job_id)
		print job_id_cmd

		# Job ID
		job_id_str="S" + job_id_cmd[1]

		# Construction of command line	
		comand_line = "python" + " "
		comand_line = comand_line + PathSeq_loc
		comand_line = comand_line + "/steps.py "
		comand_line = comand_line + cdir
		comand_line = comand_line + "/"
		comand_line = comand_line + namefile
		comand_line = comand_line + "_"
		comand_line = comand_line + str(split_id)
		comand_line = comand_line + "_spt/split.fq1 "
		comand_line = comand_line + configfile
		comand_line = comand_line + " "
		comand_line = comand_line + number_threads
		comand_line = comand_line + " "
		comand_line = comand_line + nextconfiglist
		comand_line = comand_line + " "
		comand_line = comand_line + pdir
		comand_line = comand_line + " "
		comand_line = comand_line + cdir
		comand_line = comand_line + " "
		comand_line = comand_line + namefile
		comand_line = comand_line + " "
		comand_line = comand_line + str(split_fq_cmd[1])
		comand_line = comand_line + " "
		comand_line = comand_line + str(id_step)
		comand_line = comand_line + " "
		comand_line = comand_line + compute
		comand_line = comand_line + " "
		comand_line = comand_line + namefile_o
		comand_line = comand_line + " "
		comand_line = comand_line + Institute
		comand_line = comand_line + " "
		comand_line = comand_line + PathSeq_loc
		comand_line = comand_line + " "
		comand_line = comand_line + Tmp_dir
		comand_line = comand_line + " "
		comand_line = comand_line + Java
		comand_line = comand_line + " "
		comand_line = comand_line + Bwa_loc
		comand_line = comand_line + " "
		comand_line = comand_line + Blast_loc	
		comand_line = comand_line + " "
		comand_line = comand_line + Repeatmasker_loc
		comand_line = comand_line + " "
		comand_line = comand_line + Python
		comand_line = comand_line + " "
		comand_line = comand_line + Package_loader
		comand_line = comand_line + " "
		comand_line = comand_line + Loader_file		
		comand_line = comand_line + " "
		comand_line = comand_line + Assembler_loc
		comand_line = comand_line + " "
		comand_line = comand_line + O_config		
		comand_line = comand_line + " "
		comand_line = comand_line + O_inputfile
		comand_line = comand_line + " "
		comand_line = comand_line + Samtools
		
		print comand_line

		c_command=pdir+"/Commands/"
		c_command=c_command + str(id_step)
		c_command=c_command + "_PathSeq_"
		c_command=c_command + job_id_str
		c_command=c_command + ".command"
		foutname=open(c_command,'w')


		if Package_loader == "YES":
			ff1 = open(Loader_file, 'r')
			use1 = ff1.readlines()
			ff1.close()

			for user in use1:
				liner = user.strip()
				foutname.write(liner)
				foutname.write("\n")				


			#foutname.write(Loader_file)
			#foutname.write("\n")			
			foutname.write(comand_line)
			foutname.write("\n")
			chdir="cd "+pdir
			foutname.write(chdir)
			foutname.write("\n")
			clean_file=pdir + "/clean.files"
			chmod_line="chmod 777 "+ clean_file
			foutname.write(chmod_line)
			foutname.write("\n")
			foutname.write(clean_file)
			foutname.write("\n")
		else:
			foutname.write(comand_line)
			foutname.write("\n")
			chdir="cd "+pdir
			foutname.write(chdir)
			foutname.write("\n")
			clean_file=pdir + "/clean.files"
			chmod_line="chmod 777 "+ clean_file
			foutname.write(chmod_line)
			foutname.write("\n")
			foutname.write(clean_file)
			foutname.write("\n")
			
		foutname.close()

		ch_perm = "chmod 777 "+c_command
		print ch_perm
		ch_perm_cmd=commands.getstatusoutput(ch_perm)
		print ch_perm_cmd

		#c_command=c_command + id_step
		#c_command=c_command + "_PathSeq_"
		if compute == "UGER": # prefixcommand: qsub -q long -l m_mem_free=8g
			job_sub = prefix_command +" -N "
			job_sub = job_sub + job_id_str
			job_sub = job_sub + " -o "
			job_sub = job_sub + pdir
			job_sub = job_sub + "/Logs/"
			job_sub = job_sub + str(id_step)
			job_sub = job_sub + "_"
			job_sub = job_sub + str(split_id)
			job_sub = job_sub + "_PathSeq_log.txt "
			#job_sub = job_sub + namefile
			#job_sub = job_sub + "_"
			#job_sub = job_sub + str(split_id)
			#job_sub = job_sub + "_spt/log.txt "
			job_sub = job_sub + "-e "
			job_sub = job_sub + pdir
			job_sub = job_sub + "/"
			job_sub = job_sub + "/Logs/"
			job_sub = job_sub + id_step
			job_sub = job_sub + "_"
			job_sub = job_sub + str(split_id)
			job_sub = job_sub + "_PathSeq_error.txt "
			#job_sub = job_sub + namefile
			#job_sub = job_sub + "_"
			#job_sub = job_sub + str(split_id)
			#job_sub = job_sub + "_spt/error.txt "			
			job_sub = job_sub + c_command
			print job_sub
			
			job_sub_cmd=commands.getstatusoutput(job_sub)
			print job_sub_cmd
		else:
			if compute == "LSF":# prefixcommand: bsub -q hour -R "rusage[mem=2]" 
				job_sub = prefix_command +" -P "
				job_sub = job_sub + "PathSeq -J "
				job_sub = job_sub + job_id_str
				job_sub = job_sub + " -o "
				job_sub = job_sub + pdir
				job_sub = job_sub + "/Logs/"
				job_sub = job_sub + str(id_step)
				job_sub = job_sub + "_"
				job_sub = job_sub + str(split_id)
				job_sub = job_sub + "_PathSeq_log.txt "
				#job_sub = job_sub + namefile
				#job_sub = job_sub + "_"
				#job_sub = job_sub + str(split_id)
				#job_sub = job_sub + "_spt/log.txt "
				job_sub = job_sub + "-e "
				job_sub = job_sub + pdir
				job_sub = job_sub + "/Logs/"
				job_sub = job_sub + str(id_step)
				job_sub = job_sub + "_"
				job_sub = job_sub + str(split_id)
				job_sub = job_sub + "_PathSeq_error.txt "
				#job_sub = job_sub + namefile
				#job_sub = job_sub + "_"
				#job_sub = job_sub + str(split_id)
				#job_sub = job_sub + "_spt/error.txt "			
				job_sub = job_sub + c_command
				print job_sub

				job_sub_cmd=commands.getstatusoutput(job_sub)
				print job_sub_cmd				
			else:
				if compute == "SGE": # prefixcommand: bsub -q hour -R "rusage[mem=2]" 
					print "SGE"
					job_sub = "qsub -q "+queue
					job_sub = job_sub + " -l h_vmem="
					job_sub = job_sub + memory_size
					job_sub = job_sub + "G -N "
					job_sub = job_sub + job_id_str
					job_sub = job_sub + " -o "
					job_sub = job_sub + cdir
					job_sub = job_sub + "/"
					job_sub = job_sub + namefile
					job_sub = job_sub + "_"
					job_sub = job_sub + str(split_id)
					job_sub = job_sub + "_spt/log.txt "
					job_sub = job_sub + "-e "
					job_sub = job_sub + cdir
					job_sub = job_sub + "/"
					job_sub = job_sub + namefile
					job_sub = job_sub + "_"
					job_sub = job_sub + str(split_id)
					job_sub = job_sub + "_spt/error.txt "			
					job_sub = job_sub + c_command
					print job_sub

					job_sub_cmd=commands.getstatusoutput(job_sub)
					print job_sub_cmd
				else:
					if compute=="SBATCH": # Prefixcommand: sbatch 00cpus-per-task-20 -mem=24g
						job_sub = prefix_command +" --output "
						job_sub = job_sub + cdir
						job_sub = job_sub + "/"
						job_sub = job_sub + namefile
						job_sub = job_sub + "_"
						job_sub = job_sub + str(split_id)
						job_sub = job_sub + "_spt/log.txt "
						job_sub = job_sub + "--error "
						job_sub = job_sub + cdir
						job_sub = job_sub + "/"
						job_sub = job_sub + namefile
						job_sub = job_sub + "_"
						job_sub = job_sub + str(split_id)
						job_sub = job_sub + "_spt/error.txt "			
						job_sub = job_sub + c_command
						print job_sub

						job_sub_cmd=commands.getstatusoutput(job_sub)
						print job_sub_cmd


end_time = time.time()
timetaken= (end_time - start_time)
print "Time Taken:"
print timetaken
