std::string zipName= "ar"+runTime.timeName()+".tar";
if(Pstream::parRun() && ToArchive)
{
	std::string processor= "processor"+std::to_string(Pstream::myProcNo());
	std::string cmd1= "cd "+processor+"; ";
	std::string cmd2= "tar -cf "+zipName+" "+runTime.timeName()+"; ";
	std::string cmd3= "rm -r "+runTime.timeName()+"; ";
	
	system (cmd1+cmd2+cmd3);
	Info<< "Archiving all time folders in each processor folder .......  " << endl;
}
