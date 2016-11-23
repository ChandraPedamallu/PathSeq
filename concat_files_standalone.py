#!/usr/bin/env python
# Created: Chandra Sekhar Pedamallu, DFCI, The Broad Institute
# Email : pcs.murali@gmail.com
# Purpose: PathSeq V2.0 pipeline
# Updates: Concatenate output files Standalone
# DFCI / Broad Institute@ copyright

import sys
import os
import commands
import random
import time

start_time = time.time()

print "CONCATENATE\n"


#Arguments
args=sys.argv
print "Step 0: Read config, premegablast, megablast, and blastn config files"
# Strip off spaces infornt and behing the lines and get file name
namefile = args[1].strip() # Read in FQ1 format
configfile = args[2].strip()
pdir=args[3].strip()
cdir=args[4].strip()
id_step=args[5].strip()
namefile_o=args[6].strip()
mergesamjar=args[7].strip()
javaloc=args[8].strip()
tmpdir=args[9].strip()
Samtools=args[10].strip()


mkdir_file = "mkdir " +cdir 
mkdir_file = mkdir_file + "/"
mkdir_file = mkdir_file + "combine_results"
print mkdir_file
mkdir_file_cmd=commands.getstatusoutput(mkdir_file)
print mkdir_file_cmd

ff = open(configfile, 'r')
database = ff.readlines()
ff.close()

dbindex=0

# Write the respective database file into config files and upload them
for no_databases1 in database:
	dbindex = dbindex + 1
	line = no_databases1.strip()
	data_split=line.split(":")
	print data_split
	
	if data_split[0] == "BWA":

		print "BWA  Concate";
		concate_files = "cat " + cdir
		concate_files = concate_files +"/"
		concate_files = concate_files +"*.bwa."
		concate_files = concate_files +str(id_step)
		concate_files = concate_files +"_"
		concate_files = concate_files +str(dbindex)
		concate_files = concate_files +".stat >"
		concate_files = concate_files + cdir
		concate_files = concate_files +"/"
		concate_files = concate_files +"combine_results/BWA."
		concate_files = concate_files +str(id_step)
		concate_files = concate_files +"_"
		concate_files = concate_files +str(dbindex)
		concate_files = concate_files +".stat"
		print concate_files
		concate_files_cmd=commands.getstatusoutput(concate_files)
		print concate_files_cmd
		
		concate_files = "cat " + cdir
		concate_files = concate_files +"/"
		concate_files = concate_files +"*"
		concate_files = concate_files +".unmappedbwa.fq1."
		concate_files = concate_files +str(id_step)
		concate_files = concate_files +"_"
		concate_files = concate_files +str(dbindex)
		concate_files = concate_files +" >"
		concate_files = concate_files + cdir
		concate_files = concate_files +"/"
		concate_files = concate_files +"combine_results/"
		concate_files = concate_files +"BWA"
		concate_files = concate_files +".unmappedbwa.fq1."
		concate_files = concate_files +str(id_step)
		concate_files = concate_files +"_"
		concate_files = concate_files +str(dbindex)			
		print concate_files
		concate_files_cmd=commands.getstatusoutput(concate_files)
		print concate_files_cmd	
		
		# List all Samfiles in the directory into a file
		lst_samfiles = "ls " + cdir
		lst_samfiles = lst_samfiles +"/"
		lst_samfiles = lst_samfiles +"*"
		lst_samfiles = lst_samfiles +"."
		lst_samfiles = lst_samfiles +str(id_step)
		lst_samfiles = lst_samfiles +"_"
		lst_samfiles = lst_samfiles +str(dbindex)
		lst_samfiles = lst_samfiles +".aln.sam | xargs -n 1  > "
		lst_samfiles = lst_samfiles + cdir
		lst_samfiles = lst_samfiles +"/"
		lst_samfiles = lst_samfiles +"combine_results/"
		lst_samfiles = lst_samfiles +"lstSamfiles."
		lst_samfiles = lst_samfiles +str(id_step)
		lst_samfiles = lst_samfiles +"_"
		lst_samfiles = lst_samfiles +str(dbindex)
		print "**************************"
		print lst_samfiles
		print "**************************"
		lst_samfiles_cmd=commands.getstatusoutput(lst_samfiles)
		print lst_samfiles_cmd
		
		# File name with list of samfiles
		lst_sam_filename=cdir + "/"
		lst_sam_filename=lst_sam_filename+"combine_results/"
		lst_sam_filename=lst_sam_filename+"lstSamfiles."
		lst_sam_filename=lst_sam_filename+str(id_step)
		lst_sam_filename=lst_sam_filename+"_"
		lst_sam_filename=lst_sam_filename+str(dbindex)
		print lst_sam_filename
		
		# Read the File with list of samfiles
		flst_samfiles = open(lst_sam_filename, 'r')
		samfile_list = flst_samfiles.readlines()
		print samfile_list

		# Merge the sam files	
		mergesam_cmd= Samtools + " merge "
		mergesam_cmd= mergesam_cmd + cdir
		mergesam_cmd= mergesam_cmd + "/combine_results/"
		mergesam_cmd= mergesam_cmd + "BWAalignedsamfile."
		mergesam_cmd= mergesam_cmd + str(id_step)
		mergesam_cmd= mergesam_cmd + "_"
		mergesam_cmd= mergesam_cmd + str(dbindex)
		mergesam_cmd= mergesam_cmd + ".sam"
		for samfile_list1 in samfile_list:
			line_samfilelst = samfile_list1.strip()
			mergesam_cmd=mergesam_cmd+" "
			mergesam_cmd=mergesam_cmd+line_samfilelst
		
		#mergesam_cmd= javaloc + " -jar "
		#mergesam_cmd= mergesam_cmd + mergesamjar
		#mergesam_cmd= mergesam_cmd + " TMP_DIR="
		#mergesam_cmd= mergesam_cmd + tmpdir
		#mergesam_cmd= mergesam_cmd + " VALIDATION_STRINGENCY=SILENT OUTPUT="
		#mergesam_cmd= mergesam_cmd + cdir
		#mergesam_cmd= mergesam_cmd + "/combine_results/"
		#mergesam_cmd= mergesam_cmd + "BWAalignedsamfile."
		#mergesam_cmd= mergesam_cmd + str(id_step)
		#mergesam_cmd= mergesam_cmd + "_"
		#mergesam_cmd= mergesam_cmd + str(dbindex)
		#mergesam_cmd= mergesam_cmd + ".sam"
		
		#for samfile_list1 in samfile_list:
		#	line_samfilelst = samfile_list1.strip()
		#	mergesam_cmd=mergesam_cmd+" INPUT="
		#	mergesam_cmd=mergesam_cmd+line_samfilelst
		ff.close()
		
		mergesam_run=commands.getstatusoutput(mergesam_cmd)
		print mergesam_run		

	elif data_split[0] == "MEGABLAST":
		print "MEGABLAST Concate";

		filename=cdir + "/"
		filename=filename + "combine_results/"
		filename=filename +"Megablast"
		filename=filename +".mega.annotate.hittable."
		filename=filename +str(id_step)
		filename=filename +"_"
		filename=filename +str(dbindex)			
		finaloutname=open(filename,'w')
		finaloutname.write("Read_Name\tRead_Length\tHit_numb\tSubject_id\tMapped_Subject\tSubject_Acession_Number\tSubject_Length\tBit_score\tE-value\tHSP_hit_starts\tHSP_hit_ends\tHSP_Identity\tHSP_alignlength\tPercentage_identity\tQuery_coverage\tHSP_start\tHSP_end\tAlignedSeq\tFullQuery\tKingdom\tSubjectName\n")
		finaloutname.close()



		concate_files = "cat " + cdir
		concate_files = concate_files +"/"
		concate_files = concate_files +namefile
		concate_files = concate_files +".mega.annotate.hittable."
		concate_files = concate_files +str(id_step)
		concate_files = concate_files +"_"			
		concate_files = concate_files +str(dbindex)
		concate_files = concate_files +" >> "
		concate_files = concate_files +filename

		#concate_files = concate_files + cdir
		#concate_files = concate_files +"/"
		#concate_files = concate_files +"combine_results/"
		#concate_files = concate_files +"Megablast"
		#concate_files = concate_files +".annotate.hittable."
		#concate_files = concate_files +str(id_step)
		#concate_files = concate_files +"_"			
		#concate_files = concate_files +str(dbindex)			
		print concate_files
		concate_files_cmd=commands.getstatusoutput(concate_files)
		print concate_files_cmd

		concate_files = "cat " + cdir
		concate_files = concate_files +"/"
		concate_files = concate_files +namefile
		concate_files = concate_files +".unmappedmega.fq1."
		concate_files = concate_files +str(id_step)
		concate_files = concate_files +"_"
		concate_files = concate_files +str(dbindex)
		concate_files = concate_files +" >"
		concate_files = concate_files + cdir
		concate_files = concate_files +"/"
		concate_files = concate_files +"combine_results/"
		#concate_files = concate_files +namefile
		concate_files = concate_files + "Megablast"
		concate_files = concate_files +".unmappedmega.fq1."
		concate_files = concate_files +str(id_step)
		concate_files = concate_files +"_"			
		concate_files = concate_files +str(dbindex)			
		print concate_files
		concate_files_cmd=commands.getstatusoutput(concate_files)
		print concate_files_cmd

		concate_files = "cat " + cdir
		concate_files = concate_files +"/"
		concate_files = concate_files +namefile
		concate_files = concate_files +".mappedmega.fq1."
		concate_files = concate_files +str(id_step)
		concate_files = concate_files +"_"
		concate_files = concate_files +str(dbindex)
		concate_files = concate_files +" >"
		concate_files = concate_files + cdir
		concate_files = concate_files +"/"
		concate_files = concate_files +"combine_results/"
		#concate_files = concate_files +namefile
		concate_files = concate_files +"Megablast"
		concate_files = concate_files +".mappedmega.fq1."
		concate_files = concate_files +str(id_step)
		concate_files = concate_files +"_"
		concate_files = concate_files +str(dbindex)			
		print concate_files
		concate_files_cmd=commands.getstatusoutput(concate_files)
		print concate_files_cmd

	elif data_split[0] == "BLASTN":
		print "BLASTN Concate";
		filename=cdir + "/"
		filename=filename + "combine_results/"
		filename=filename +"Blastn"
		filename=filename +".blastn.annotate.hittable."
		filename=filename +str(id_step)
		filename=filename +"_"
		filename=filename +str(dbindex)			
		finaloutname=open(filename,'w')
		finaloutname.write("Read_Name\tRead_Length\tHit_numb\tSubject_id\tMapped_Subject\tSubject_Acession_Number\tSubject_Length\tBit_score\tE-value\tHSP_hit_starts\tHSP_hit_ends\tHSP_Identity\tHSP_alignlength\tPercentage_identity\tQuery_coverage\tHSP_start\tHSP_end\tAlignedSeq\tFullQuery\tKingdom\tSubjectName\n")
		finaloutname.close()

		concate_files = "cat " + cdir
		concate_files = concate_files +"/"
		concate_files = concate_files +namefile
		concate_files = concate_files +".blastn.annotate.hittable."
		concate_files = concate_files +str(id_step)
		concate_files = concate_files +"_"
		concate_files = concate_files +str(dbindex)
		concate_files = concate_files +" >> "
		concate_files = concate_files +filename
		#concate_files = concate_files + cdir
		#concate_files = concate_files +"/"
		#concate_files = concate_files +"combine_results/"
		#concate_files = concate_files +"Blastn"
		#concate_files = concate_files +".blastn.annotate.hittable."
		#concate_files = concate_files +str(id_step)
		#concate_files = concate_files +"_"
		#concate_files = concate_files +str(dbindex)			
		print concate_files
		concate_files_cmd=commands.getstatusoutput(concate_files)
		print concate_files_cmd

		concate_files = "cat " + cdir
		concate_files = concate_files +"/"
		concate_files = concate_files +namefile
		concate_files = concate_files +".unmappedblastn.fq1."
		concate_files = concate_files +str(id_step)
		concate_files = concate_files +"_"
		concate_files = concate_files +str(dbindex)
		concate_files = concate_files +" >"
		concate_files = concate_files + cdir
		concate_files = concate_files +"/"
		concate_files = concate_files +"combine_results/"
		#concate_files = concate_files +namefile
		concate_files = concate_files +"Blastn"
		concate_files = concate_files +".unmappedblastn.fq1."
		concate_files = concate_files +str(id_step)
		concate_files = concate_files +"_"
		concate_files = concate_files +str(dbindex)			
		print concate_files
		concate_files_cmd=commands.getstatusoutput(concate_files)
		print concate_files_cmd

		concate_files = "cat " + cdir
		concate_files = concate_files +"/"
		concate_files = concate_files +namefile
		concate_files = concate_files +".mappedblastn.fq1."
		concate_files = concate_files +str(id_step)
		concate_files = concate_files +"_"
		concate_files = concate_files +str(dbindex)
		concate_files = concate_files +" >"
		concate_files = concate_files + cdir
		concate_files = concate_files +"/"
		concate_files = concate_files +"combine_results/"
		#concate_files = concate_files +namefile
		concate_files = concate_files +"Blastn"
		concate_files = concate_files +".mappedblastn.fq1."
		concate_files = concate_files +str(id_step)
		concate_files = concate_files +"_"
		concate_files = concate_files +str(dbindex)			
		print concate_files
		concate_files_cmd=commands.getstatusoutput(concate_files)
		print concate_files_cmd				

				
	elif data_split[0] == "REPEATMASKER":
		print "REPEATMASKER CONCATE";

		concate_files = "cat " + cdir
		concate_files = concate_files +"/"
		concate_files = concate_files +namefile
		concate_files = concate_files +".afterrep.fq1"
		concate_files = concate_files +" >"
		concate_files = concate_files + cdir
		concate_files = concate_files +"/"
		concate_files = concate_files +"combine_results/"
		#concate_files = concate_files +namefile
		concate_files = concate_files +"RepeatMasker"
		concate_files = concate_files +".afterrep.fq1"
		print concate_files
		concate_files_cmd=commands.getstatusoutput(concate_files)
		print concate_files_cmd	

	elif data_split[0] == "PREMEGABLAST":
		print "PREMEGABLAST Concate";
		filename=cdir + "/"
		filename=filename + "combine_results/"
		filename=filename +"Premegablast"
		filename=filename +".premega.annotate.hittable."
		filename=filename +str(id_step)
		filename=filename +"_"
		filename=filename +str(dbindex)			
		finaloutname=open(filename,'w')
		finaloutname.write("Read_Name\tRead_Length\tHit_numb\tSubject_id\tMapped_Subject\tSubject_Acession_Number\tSubject_Length\tBit_score\tE-value\tHSP_hit_starts\tHSP_hit_ends\tHSP_Identity\tHSP_alignlength\tPercentage_identity\tQuery_coverage\tHSP_start\tHSP_end\tAlignedSeq\tFullQuery\tKingdom\tSubjectName\n")
		finaloutname.close()

		concate_files = "cat " + cdir
		concate_files = concate_files +"/"
		concate_files = concate_files +namefile
		concate_files = concate_files +".premega.annotate.hittable."
		concate_files = concate_files +str(id_step)
		concate_files = concate_files +"_"
		concate_files = concate_files +str(dbindex)
		concate_files = concate_files +" >> "
		concate_files = concate_files +filename

		#concate_files = concate_files + cdir
		#concate_files = concate_files +"/"
		#concate_files = concate_files +"combine_results/"
		#concate_files = concate_files +"Premegablast"
		#concate_files = concate_files +".premega.annotate.hittable."
		#concate_files = concate_files +str(id_step)
		#concate_files = concate_files +"_"
		#concate_files = concate_files +str(dbindex)			
		print concate_files
		concate_files_cmd=commands.getstatusoutput(concate_files)
		print concate_files_cmd

		concate_files = "cat " + cdir
		concate_files = concate_files +"/"
		concate_files = concate_files +namefile
		concate_files = concate_files +".unmappedpremega.fq1."
		concate_files = concate_files +str(id_step)
		concate_files = concate_files +"_"
		concate_files = concate_files +str(dbindex)
		concate_files = concate_files +" >"
		concate_files = concate_files + cdir
		concate_files = concate_files +"/"
		concate_files = concate_files +"combine_results/"
		#concate_files = concate_files +namefile
		concate_files = concate_files +"Premegablast"
		concate_files = concate_files +".unmappedpremega.fq1."
		concate_files = concate_files +str(id_step)
		concate_files = concate_files +"_"
		concate_files = concate_files +str(dbindex)			
		print concate_files
		concate_files_cmd=commands.getstatusoutput(concate_files)
		print concate_files_cmd

		concate_files = "cat " + cdir
		concate_files = concate_files +"/"
		concate_files = concate_files +namefile
		concate_files = concate_files +".mappedpremega.fq1."
		concate_files = concate_files +str(id_step)
		concate_files = concate_files +"_"
		concate_files = concate_files +str(dbindex)
		concate_files = concate_files +" >"
		concate_files = concate_files + cdir
		concate_files = concate_files +"/"
		concate_files = concate_files +"combine_results/"
		#concate_files = concate_files +namefile
		concate_files = concate_files +"Premegablast"
		concate_files = concate_files +".mappedpremega.fq1."
		concate_files = concate_files +str(id_step)
		concate_files = concate_files +"_"
		concate_files = concate_files +str(dbindex)			
		print concate_files
		concate_files_cmd=commands.getstatusoutput(concate_files)
		print concate_files_cmd							

	elif data_split[0] == "BLASTX":
		print "BLASTX Concate"

		filename=cdir + "/"
		filename=filename + "combine_results/"
		filename=filename +"Blastx"
		filename=filename +".blastx.annotate.hittable."
		filename=filename +str(id_step)
		filename=filename +"_"
		filename=filename +str(dbindex)			
		finaloutname=open(filename,'w')
		finaloutname.write("Read_Name\tRead_Length\tHit_numb\tSubject_id\tMapped_Subject\tSubject_Acession_Number\tSubject_Length\tBit_score\tE-value\tHSP_hit_starts\tHSP_hit_ends\tHSP_Identity\tHSP_alignlength\tPercentage_identity\tQuery_coverage\tHSP_start\tHSP_end\tAlignedSeq\tFullQuery\tKingdom\tSubjectName\n")
		finaloutname.close()
		concate_files = "cat " + cdir
		concate_files = concate_files +"/"
		concate_files = concate_files +namefile
		concate_files = concate_files +".blastx.annotate.hittable."
		concate_files = concate_files +str(id_step)
		concate_files = concate_files +"_"
		concate_files = concate_files +str(dbindex)
		concate_files = concate_files +" >>"
		concate_files = concate_files +filename
		#concate_files = concate_files + cdir
		#concate_files = concate_files +"/"
		#concate_files = concate_files +"combine_results/"
		#concate_files = concate_files +"Blastx"
		#concate_files = concate_files +".blastx.annotate.hittable."
		#concate_files = concate_files +str(id_step)
		#concate_files = concate_files +"_"
		#concate_files = concate_files +str(dbindex)			
		print concate_files
		concate_files_cmd=commands.getstatusoutput(concate_files)
		print concate_files_cmd

		concate_files = "cat " + cdir
		concate_files = concate_files +"/"
		concate_files = concate_files +namefile
		concate_files = concate_files +".unmappedblastx.fq1."
		concate_files = concate_files +str(id_step)
		concate_files = concate_files +"_"
		concate_files = concate_files +str(dbindex)
		concate_files = concate_files +" >"
		concate_files = concate_files + cdir
		concate_files = concate_files +"/"
		concate_files = concate_files +"combine_results/"
		#concate_files = concate_files +namefile
		concate_files = concate_files +"Blastx"
		concate_files = concate_files +".unmappedblastx.fq1."
		concate_files = concate_files +str(id_step)
		concate_files = concate_files +"_"
		concate_files = concate_files +str(dbindex)			
		print concate_files
		concate_files_cmd=commands.getstatusoutput(concate_files)
		print concate_files_cmd

		concate_files = "cat " + cdir
		concate_files = concate_files +"/"
		concate_files = concate_files +namefile
		concate_files = concate_files +".mappedblastx.fq1."
		concate_files = concate_files +str(id_step)
		concate_files = concate_files +"_"
		concate_files = concate_files +str(dbindex)
		concate_files = concate_files +" >"
		concate_files = concate_files + cdir
		concate_files = concate_files +"/"
		concate_files = concate_files +"combine_results/"
		concate_files = concate_files +"Blastx"
		#concate_files = concate_files +namefile
		concate_files = concate_files +".mappedblastx.fq1."
		concate_files = concate_files +str(id_step)
		concate_files = concate_files +"_"
		concate_files = concate_files +str(dbindex)			
		print concate_files
		concate_files_cmd=commands.getstatusoutput(concate_files)
		print concate_files_cmd
	elif data_split[0] == "TBLASTX":
		print "TBLASTX Concate"

		filename=cdir + "/"
		filename=filename + "combine_results/"
		filename=filename +"TBlastx"
		filename=filename +".tblastx.annotate.hittable."
		filename=filename +str(id_step)
		filename=filename +"_"
		filename=filename +str(dbindex)			
		finaloutname=open(filename,'w')
		finaloutname.write("Read_Name\tRead_Length\tHit_numb\tSubject_id\tMapped_Subject\tSubject_Acession_Number\tSubject_Length\tBit_score\tE-value\tHSP_hit_starts\tHSP_hit_ends\tHSP_Identity\tHSP_alignlength\tPercentage_identity\tQuery_coverage\tHSP_start\tHSP_end\tAlignedSeq\tFullQuery\tKingdom\tSubjectName\n")
		finaloutname.close()
		concate_files = "cat " + cdir
		concate_files = concate_files +"/"
		concate_files = concate_files +namefile
		concate_files = concate_files +".tblastx.annotate.hittable."
		concate_files = concate_files +str(id_step)
		concate_files = concate_files +"_"
		concate_files = concate_files +str(dbindex)
		concate_files = concate_files +" >>"
		concate_files = concate_files +filename
		#concate_files = concate_files + cdir
		#concate_files = concate_files +"/"
		#concate_files = concate_files +"combine_results/"
		#concate_files = concate_files +"TBlastx"
		#concate_files = concate_files +".tblastx.annotate.hittable."
		#concate_files = concate_files +str(id_step)
		#concate_files = concate_files +"_"
		#concate_files = concate_files +str(dbindex)			
		print concate_files
		concate_files_cmd=commands.getstatusoutput(concate_files)
		print concate_files_cmd

		concate_files = "cat " + cdir
		concate_files = concate_files +"/"
		concate_files = concate_files +namefile
		concate_files = concate_files +".unmappedtblastx.fq1."
		concate_files = concate_files +str(id_step)
		concate_files = concate_files +"_"
		concate_files = concate_files +str(dbindex)
		concate_files = concate_files +" >"
		concate_files = concate_files + cdir
		concate_files = concate_files +"/"
		concate_files = concate_files +"combine_results/"
		#concate_files = concate_files +namefile
		concate_files = concate_files +"TBlastx"
		concate_files = concate_files +".unmappedtblastx.fq1."
		concate_files = concate_files +str(id_step)
		concate_files = concate_files +"_"
		concate_files = concate_files +str(dbindex)			
		print concate_files
		concate_files_cmd=commands.getstatusoutput(concate_files)
		print concate_files_cmd

		concate_files = "cat " + cdir
		concate_files = concate_files +"/"
		concate_files = concate_files +namefile
		concate_files = concate_files +".mappedtblastx.fq1."
		concate_files = concate_files +str(id_step)
		concate_files = concate_files +"_"
		concate_files = concate_files +str(dbindex)
		concate_files = concate_files +" >"
		concate_files = concate_files + cdir
		concate_files = concate_files +"/"
		concate_files = concate_files +"combine_results/"
		concate_files = concate_files +"TBlastx"
		#concate_files = concate_files +namefile
		concate_files = concate_files +".mappedtblastx.fq1."
		concate_files = concate_files +str(id_step)
		concate_files = concate_files +"_"
		concate_files = concate_files +str(dbindex)			
		print concate_files
		concate_files_cmd=commands.getstatusoutput(concate_files)
		print concate_files_cmd
	elif data_split[0] == "TBLASTN":
		print "TBLASTN Concate"

		filename=cdir + "/"
		filename=filename + "combine_results/"
		filename=filename +"TBlastn"
		filename=filename +".tblastn.annotate.hittable."
		filename=filename +str(id_step)
		filename=filename +"_"
		filename=filename +str(dbindex)			
		finaloutname=open(filename,'w')
		finaloutname.write("Read_Name\tRead_Length\tHit_numb\tSubject_id\tMapped_Subject\tSubject_Acession_Number\tSubject_Length\tBit_score\tE-value\tHSP_hit_starts\tHSP_hit_ends\tHSP_Identity\tHSP_alignlength\tPercentage_identity\tQuery_coverage\tHSP_start\tHSP_end\tAlignedSeq\tFullQuery\tKingdom\tSubjectName\n")
		finaloutname.close()
		concate_files = "cat " + cdir
		concate_files = concate_files +"/"
		concate_files = concate_files +namefile
		concate_files = concate_files +".tblastn.annotate.hittable."
		concate_files = concate_files +str(id_step)
		concate_files = concate_files +"_"
		concate_files = concate_files +str(dbindex)
		concate_files = concate_files +" >>"
		concate_files = concate_files +filename
		#concate_files = concate_files + cdir
		#concate_files = concate_files +"/"
		#concate_files = concate_files +"combine_results/"
		#concate_files = concate_files +"TBlastn"
		#concate_files = concate_files +".tblastn.annotate.hittable."
		#concate_files = concate_files +str(id_step)
		#concate_files = concate_files +"_"
		#concate_files = concate_files +str(dbindex)			
		print concate_files
		concate_files_cmd=commands.getstatusoutput(concate_files)
		print concate_files_cmd

		concate_files = "cat " + cdir
		concate_files = concate_files +"/"
		concate_files = concate_files +namefile
		concate_files = concate_files +".unmappedtblastn.fq1."
		concate_files = concate_files +str(id_step)
		concate_files = concate_files +"_"
		concate_files = concate_files +str(dbindex)
		concate_files = concate_files +" >"
		concate_files = concate_files + cdir
		concate_files = concate_files +"/"
		concate_files = concate_files +"combine_results/"
		#concate_files = concate_files +namefile
		concate_files = concate_files +"TBlastn"
		concate_files = concate_files +".unmappedtblastn.fq1."
		concate_files = concate_files +str(id_step)
		concate_files = concate_files +"_"
		concate_files = concate_files +str(dbindex)			
		print concate_files
		concate_files_cmd=commands.getstatusoutput(concate_files)
		print concate_files_cmd

		concate_files = "cat " + cdir
		concate_files = concate_files +"/"
		concate_files = concate_files +namefile
		concate_files = concate_files +".mappedtblastn.fq1."
		concate_files = concate_files +str(id_step)
		concate_files = concate_files +"_"
		concate_files = concate_files +str(dbindex)
		concate_files = concate_files +" >"
		concate_files = concate_files + cdir
		concate_files = concate_files +"/"
		concate_files = concate_files +"combine_results/"
		concate_files = concate_files +"TBlastn"
		#concate_files = concate_files +namefile
		concate_files = concate_files +".mappedtblastn.fq1."
		concate_files = concate_files +str(id_step)
		concate_files = concate_files +"_"
		concate_files = concate_files +str(dbindex)			
		print concate_files
		concate_files_cmd=commands.getstatusoutput(concate_files)
		print concate_files_cmd
							
							
concatloc = "cat " + cdir
concatloc = concatloc +"/"
concatloc = concatloc +namefile
concatloc = concatloc +".unmapped*.fq1."
concatloc = concatloc +str(id_step)
concatloc = concatloc +"_"
concatloc = concatloc +str(dbindex-1)
concatloc = concatloc +" >"
concatloc = concatloc + cdir
concatloc = concatloc +"/"
concatloc = concatloc +"combine_results/"
concatloc = concatloc +namefile
concatloc = concatloc +".unmappedfinal.fq1"
print concatloc
concatloc_cmd=commands.getstatusoutput(concatloc)
print concatloc_cmd

end_time = time.time()
timetaken= (end_time - start_time)
print "Time Taken:"
print timetaken
