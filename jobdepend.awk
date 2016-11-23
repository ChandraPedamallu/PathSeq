NR==1{
	i=1; depend="";start=0;
	while(i==1){
		ii=getline line < T0;
		if(ii==0){break;}
		if(start==0){
			depend="done("line")";
		}
		else{
			depend=depend"&&done("line")";
		}
		start++;
	}
	print depend;

	exit;
}
