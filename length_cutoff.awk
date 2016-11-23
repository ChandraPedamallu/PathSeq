NR==1{
	i=1;
	uleng=T02;
	lleng=T01;
	output=T03;
	ns=0;
	while(i==1){
		ii=getline line < T0;
		if(ii==0){break;}
		nsub=substr(line, 1, 1);
		if(nsub==">"){
			if(ns>0){
				if((lenth >= lleng) && (lenth <= uleng)){
					print nameseq > output;
					for(nn=1; nn<nk; nn++){
						print lines[nn] > output;
					}
				}
			}
			nameseq=line;
			ns=1;
			lenth=0;
			nk=1;
		}
		else{
			nsk=split(line, xx, " ");
			for(j=1; j<=nsk; j++){
				lenth=lenth + length(xx[j]);
			}

			#lenth=lenth + length(line);
			lines[nk]=line;
			nk++;
		}
		
	}
	close(T0);
	if((lenth >= lleng) && (lenth <=uleng)){
		print nameseq > output;
		for(nn=1; nn<nk; nn++){
			print lines[nn] > output;
		}
	}
	
	exit;

}
