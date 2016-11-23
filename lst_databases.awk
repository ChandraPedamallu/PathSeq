# List the databases
NR==1{
	i=1;no=1;
	no_premeg=0;
	no_meg=0;
	no_blastn=0;
	no_blastx=0;
	no_cblastn=0;
	no_cblastx=0;
	no_rpstblastn=0;

	while(i==1){
		ii=getline line < T0;
		if(ii==0){break;}
		sub(/^ */, "", line);
		sub(/ *$/, "", line);

		if(length(line)>0){
			nsub=substr(line,1,1);
			if(nsub!="#"){
				ns=split(line, x, ":");
				
				ns1=split(x[3], xx, ",");
				for(kk=1; kk<=ns1; kk++){
					if(xx[kk]=="PREMEGA"){ # PREMEGABLAST
						print "PREMEGA:"x[2] > T01;	
						no_premeg++;
					}
					else if(xx[kk]=="MEGA"){# MEGABLAST
						print "MEGA:"x[2] > T01;		
						no_meg++;
					}
					else if(xx[kk]=="BLASTN"){# BLASTN
						print "BLASTN:"x[2] > T01;		
						no_blastn++;
					}
					else if(xx[kk]=="BLASTX"){ # BLASTX
						print "BLASTX:"x[2] > T01;		
						no_blastx++;
					}
					else if(xx[kk]=="CBLASTN"){ # CONTIG BLASTN
						print "CBLASTN:"x[2] > T01;		
						no_cblastn++;
					}
					else if(xx[kk]=="CBLASTX"){ # CONTIG BLASTX
						print "CBLASTX:"x[2] > T01;		
						no_cblastx++;
					}
					else if(xx[kk]=="RPSTBLASTN"){ # CONTIG RPSTBLASTN
						print "RPSTBLASTN:"x[2] > T01;		
						no_rpstblastn++;
					}

				}
			}
		}
	}
	close(T0);
	close(T01);
	
	print no_premeg;
	print no_meg;
	print no_blastn;
	print no_blastx;
	print no_cblastn;
	print no_cblastx;
	print no_rpstblastn;

	exit
}
