# List the databases T0 --- full config; T01 --- current.config; T02 --- nextconfiglist
NR==1{
	i=1;no=1;
	no_config=0; write="N";
	loc=T03
	while(i==1){
		ii=getline line < T0;
		if(ii==0){break;}
		sub(/^ */, "", line);
		sub(/ *$/, "", line);
		ns=split(line, x, ":");

		if(x[1]=="SCATTER"){
			if(no==1){
				print line > loc"/"T01;
				write="Y";
			}
			else{
				no_config++;
				print line > loc"/"no_config".config";
				print loc"/"no_config".config" > loc"/"T02;
				write="N";
			}
			
			no++;
		}
		else{
			if(write=="Y"){
				print line > loc"/"T01;
			}
			else{
				print line > loc"/"no_config".config";
			}

		}

			
	}
	close(T0);
	close(T01);
	
	print (no_config+1);

	exit
}
