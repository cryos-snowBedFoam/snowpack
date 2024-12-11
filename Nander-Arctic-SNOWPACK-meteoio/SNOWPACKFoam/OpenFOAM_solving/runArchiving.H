if(Pstream::parRun() && ToArchive && runTime.outputTime())
{		
	std::string processor= "processor"+std::to_string(Pstream::myProcNo());
	std::string cmd1= "cd "+processor+"; ";
	//std::string cmd2= "tar -rf ar.tar "+runTime.timeName()+"; ";
	std::string cmd2= "tar -rf "+zipName+" "+runTime.timeName()+"; ";
	std::string cmd3= "rm -r "+runTime.timeName()+"; ";
	
	system (cmd1+cmd2+cmd3);
	//system (cmd1+cmd2); //works with purge as 1
	Info<< "Archiving all time folders in each processor folder .......  " << endl;			
}
