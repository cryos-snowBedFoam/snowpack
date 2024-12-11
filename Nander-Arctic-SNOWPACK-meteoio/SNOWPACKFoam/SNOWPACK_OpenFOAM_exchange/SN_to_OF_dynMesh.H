{
	const size_t nN = Xdata.getNumberOfNodes();
	size_t nE = nN-1;

	const pointField& ctrs = mesh.cellCentres();
	
	/*
	/// add elements according to deltaZ=Z2-Z1
    scalar Z1 =gMax(mesh.points()).z()-initialCellSize_z;
	scalar Z2=NDS[Xdata.getNumberOfNodes()-1].z;
	scalar deltaZ=Z2-Z1;
	scalar maxMovement=	0.00025;//(minThickness*0.5);    
	label iterationNumber=label(mag(deltaZ/maxMovement))+1;
	scalar subDeltaZ= deltaZ/scalar(iterationNumber);
	scalar sum_dZ=0.;
	
	snowHeight_old_OF=gMax(ctrs).z();
	topPatchDisplacement[0]=subDeltaZ;	
    Info << "------SN_to_OF_dynMesh---------------the snow height change/snowHeight_old_OF:  " <<  deltaZ << ' ' << snowHeight_old_OF << endl;	
	
	mesh.moving(movingMeshReal); /// Note: to consider fixed-mesh stretaegy for OpenFOAM-SNOWPACK coupling		
	*/ 
				
	/// add elements according to deltaZ=Z2-Z1
    scalar Z1 =0.0;
	scalar Z2=0.0;
	scalar deltaZ=0.0;
	scalar maxMovement=	0.00025;//(minThickness*0.5);    
	label iterationNumber=0;
	scalar subDeltaZ= 0.;
	scalar sum_dZ=0.;	
	if( Pstream::master() )
	{
		Z1 =max(mesh.points()).z()-initialCellSize_z;
		Z2=NDS[Xdata.getNumberOfNodes()-1].z;
		deltaZ=Z2-Z1;
		iterationNumber=label(mag(deltaZ/maxMovement))+1;
		subDeltaZ= deltaZ/scalar(iterationNumber);
	}
	reduce(Z1, sumOp<scalar>()); /// Collect across all processors
	reduce(Z2, sumOp<scalar>()); /// Collect across all processors
	reduce(deltaZ, sumOp<scalar>()); /// Collect across all processors
	reduce(iterationNumber, sumOp<label>()); /// Collect across all processors
	reduce(subDeltaZ, sumOp<scalar>()); /// Collect across all processors

	
	snowHeight_old_OF=gMax(ctrs).z();
	topPatchDisplacement[0]=subDeltaZ;	
    Info << "------SN_to_OF_dynMesh---------------the snow height change/snowHeight_old_OF:  " <<  deltaZ << ' ' << snowHeight_old_OF << endl;	
	
	mesh.moving(movingMeshReal); /// Note: we need at the start of dynMesh loop to use mesh.moving(movingMeshReal) to do add/remove mesh layers because it was set previously falsely false.		

	for (label i=0; i<iterationNumber; i++)
	{
		sum_dZ+=subDeltaZ;

		label patchId = mesh.boundaryMesh().findPatchID("top");
		const polyPatch& patchPP = mesh.boundaryMesh()["top"];//const polyPatch& patchPP = mesh.boundaryMesh()[patchId];
		const labelList& labelPatchPoints(patchPP.meshPoints());
		pointField meshPoints(mesh.points());
		//Pout<< " number points " <<  meshPoints.size()	<< endl;
					
		forAll(labelPatchPoints,id)
		{
			meshPoints[labelPatchPoints[id]].z()+=subDeltaZ;
		}
		
		/// Note: if we use topBoundaryLayerAdditionRemovalTopoFvMesh1 we need "mesh.movePoints(meshPoints);" next line,
		/// otherwise for topBoundaryLayerAdditionRemovalTopoFvMesh2, we need to comment it:
		barrierMPI();mesh.setPhi()=dimensionedScalar("zeroMeshPhi", dimArea*dimVelocity,0.);			
		mesh.movePoints(meshPoints);
		mesh.update();    		
	}
	/// Note: most of ddt operators and maybe others for dynamicMesh use mesh.Phi() and also old sub-cycle volume Vsc0.
	/// Hence, to consider fixed-mesh strategy for OpenFOAM-SNOWPACK coupling we need to set mesh.phi() as zero and avoid using
	/// Vsc0 instead of Vsc. Check EulerDdtScheme.C for example. First, we need public boolean movingMeshReal to do the dynamic mesh stuff, then by using
	/// mesh.moving(false), we use the fixed-mesh stretaegy. Note that by mesh.moving(false) at the end we just use the fixed-mesh strategy 
	/// hoewver, we need at the start of dynMesh loop to use mesh.moving(movingMeshReal) to do add/remove mesh layers because it was set previously falsely false. 			
	//Info << "is mesh moving really? " << mesh.moving() << endl;
	movingMeshReal=mesh.moving(); mesh.moving(false); mesh.setPhi()=dimensionedScalar("zeroMeshPhi", dimArea*dimVelocity,0.);		
	//Info << "mesh is moving but gets fake state " << mesh.moving() << endl;

	gh = (g & mesh.C()) - ghRef;
    ghf = (g & mesh.Cf()) - ghRef;

    runTime.write();
}
