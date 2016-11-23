# Developed : Chandra Pedamallu, Broad Institute, DFCI
# Check the environment parameters
NR==1{
	i=1;
	
	Java_files=""
	PathSeq_dir=""
	tmpdir=""
	sam_jarfile=""
	javaloc=""
	bwaloc=""
	blastloc=""
	repeatmaskerloc=""
	pythonloc=""
	sortloc=""
	cploc=""
	wcloc=""
	concatloc=""
	mkdirloc=""
	mvloc=""
	usecmd=""
	premega_thres=""
	blastn_thres=""
	megablast_thres=""
	blastx_thres=""	

	while(i==1){
		ii=getline line < T0;
		if(ii==0){break;}
		sub(/^ */, "", line);
		sub(/ *$/, "", line);
		
		if(length(line) > 0){
			ns=split(line, x, "=");
			nsub=substr(line, 1, 1);
			sub(/^ */, "", x[1]);
			sub(/ *$/, "", x[1]);			
			if(nsub!="#"){	
				if(x[1]=="Institute"){
					Institute=x[2];
				}
				else if(x[1]=="Java_files"){
					Java_files=x[2];
				}
				else if(x[1]=="PathSeq_dir"){
					PathSeq_dir=x[2];
				}				
				else if(x[1]=="tmpdir"){
					tmpdir=x[2];
				}
				else if(x[1]=="sam_jarfile"){
					sam_jarfile=x[2];
				}				
				else if(x[1]=="javaloc"){
					javaloc=x[2];
				}
				else if(x[1]=="bwaloc"){
					bwaloc=x[2];
				}
				else if(x[1]=="blastloc"){
					blastloc=x[2];
				}
				else if(x[1]=="repeatmaskerloc"){
					repeatmaskerloc=x[2];
				}
				else if(x[1]=="pythonloc"){
					pythonloc=x[2];
				}
				else if(x[1]=="sortloc"){
					sortloc=x[2];
				}
				else if(x[1]=="cploc"){
					cploc=x[2];
				}
				else if(x[1]=="wcloc"){
					wcloc=x[2];
				}
				else if(x[1]=="concatloc"){
					concatloc=x[2];
				}
				else if(x[1]=="mkdirloc"){
					mkdirloc=x[2];
				}
				else if(x[1]=="mvloc"){
					mvloc=x[2];
				}
				else if(x[1]=="usecmd"){
					usecmd=x[2];
				}
				else if(x[1]=="premega_thres"){
					premega_thres=x[2];
				}
				else if(x[1]=="blastn_thres"){
					blastn_thres=x[2];
				}
				else if(x[1]=="megablast_thres"){
					megablast_thres=x[2];
				}
				else if(x[1]=="blastx_thres"){
					blastx_thres=x[2];
				}
				else if(x[1]=="bwaloc"){
					bwaloc=x[2];
				}
			}
		}

	}
	close(T0);
	print Institute;	
	print Java_files;
	print PathSeq_dir;
	print tmpdir;
	print sam_jarfile;
	print javaloc;
	print bwaloc;
	print blastloc;
	print repeatmaskerloc;
	print pythonloc;
	#print sortloc;
	#print cploc;
	#print wcloc;
	#print concatloc;
	#print mkdirloc;
	#print mvloc;
	print usecmd;
	print premega_thres;
	print blastn_thres;
	print megablast_thres;
	print blastx_thres;	

	exit
}
