label patchIdTop = mesh.boundaryMesh().findPatchID("top");
label patchIdBottom= mesh.boundaryMesh().findPatchID("bottom");

coupler::SnowpackInterface coupling;
SnowStation& Xdata=coupling.vecXdata[0];
std::vector<NodeData>& NDS = Xdata.Ndata;
std::vector<ElementData>& EMS = Xdata.Edata;

const size_t nN = Xdata.getNumberOfNodes();
size_t nE = nN-1;

scalar SN_deltaT=M_TO_S(coupling.calculation_step_length);
bool doNextStep_SN=true;
bool doNextStep_OF=true;
scalar cum_OF_deltaT=0.;
/*
scalar byPass_threshold=900.; /// Jafari added for by-pass
Info<< "Reading the threshold after which the flow fileds solving get stoppted to avoid computational time";
transportProperties.lookup("byPass_threshold") >> byPass_threshold;
Info<< " : " << byPass_threshold << "\n" << endl;	
*/


if(runTime.timeName()=="0")
{
	/// first, moving all mesh points to the SNOWPACK reference
	mesh.moving(movingMeshReal); /// Note: we need at the start of dynMesh loop to use mesh.moving(movingMeshReal) to do add/remove mesh layers because it was set previously falsely false.
	mesh.movePoints(mesh.points()+point(0.,0.,NDS[Xdata.SoilNode].z));
	mesh.update();
	movingMeshReal=mesh.moving(); mesh.moving(false); mesh.setPhi()=dimensionedScalar("zeroMeshPhi", dimArea*dimVelocity,0.); /// Note: to consider fixed-mesh stretaegy for OpenFOAM-SNOWPACK coupling	
	//mesh.write(); // this cause writing the point at time 0 making problem
}
else
{
	mesh.moving(movingMeshReal); /// Note: we need at the start of dynMesh loop to use mesh.moving(movingMeshReal) to do add/remove mesh layers because it was set previously falsely false.
	movingMeshReal=mesh.moving(); mesh.moving(false); mesh.setPhi()=dimensionedScalar("zeroMeshPhi", dimArea*dimVelocity,0.); /// Note: to consider fixed-mesh stretaegy for OpenFOAM-SNOWPACK coupling		
	//deltaThetaIce_accum=snowDen_accum/rho_ice;
}	

//scalar initialCellSize_z=mag(gMax(mesh.points()).z()-gMin(mesh.points()).z()) ;
Info << "The initial cell size in x direction OpenFOAM: " << initialCellSize_x << "\n" << endl; 

Info << "The initial cell size in z direction OpenFOAM: " << initialCellSize_z << "\n" << endl; 

scalar snowHeight_old_OF=gMax(mesh.cellCentres()).z() ;
Info << "The height for the first snow element in OpenFOAM: " << snowHeight_old_OF << "\n" << endl; 

Info << "The height for the first snow node in OpenFOAM: " << gMin(mesh.points()).z() 
     << " is now matched with the one in SNOWPACK: " << NDS[Xdata.SoilNode].z << "\n" << endl; 


//label NEx_OF_procc=mesh.cellCentres().size();
//label NEx_OF=NEx_OF_procc*Pstream::nProcs();
label NEx_OF_procc=label(L_x/initialCellSize_x)/Pstream::nProcs(); ///this is better for restart 
label NEx_OF=NEx_OF_procc*Pstream::nProcs();


if(NEx_OF%Pstream::nProcs()!=0)
{
	FatalErrorIn
	(
		"in file createSNOWPACK.H:\n"
	)	<< "The initial OpenFOAM mesh must be as: "
		<< "1) Just one layer of mesh in z direction \n"
		<< "2) The total number of mesh in x-direction should be divisible by the number of processors \n"	 
		<< "The total number of mesh in x-direction: " << NEx_OF << nl
		<< "The total number processors: " << Pstream::nProcs() << nl
	    << nl << exit(FatalError);
}
Info << "The total number of mesh in x-direction: " << NEx_OF << nl
	 << "The number of cells for each processor in x-direction: " << NEx_OF_procc << nl 
	 << "The total number processors: " << Pstream::nProcs() << "\n" << endl; 
	 

//J test
/*
DynamicList<scalar>  xLocHet;
scalar start_het=0.5;
scalar spacing_het=0.5;
scalar radius_het=0.0;
label number_het=mag(gMax(mesh.points()).x()-gMin(mesh.points()).x())/spacing_het+1;
//
for(label i=0; i<=number_het; i++)   
{
	scalar xloc=start_het+scalar(i)*spacing_het;
	//if (xloc>=mesh.cellCentres()[0].x() && xloc<=mesh.cellCentres()[NEx_OF_procc-2].x())
	//{
		xLocHet.append(xloc);
	//}	
}
//
forAll(xLocHet, i)
{
	Pout << "i/xloc " << i << ' ' << xLocHet[i] << endl;
}	
*/
