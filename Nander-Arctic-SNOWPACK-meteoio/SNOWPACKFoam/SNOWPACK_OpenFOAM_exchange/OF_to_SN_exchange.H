if(Xdata.getNumberOfNodes()-1>Xdata.SoilNode ) /// snow on the ground
{
	const size_t nN = Xdata.getNumberOfNodes();
	size_t nE = nN-1;

	const pointField& ctrs = mesh.cellCentres();
	
	label NEz_OF=ctrs.size()/NEx_OF_procc;
    Info << "------OF_to_SN_exchange---------------NEz_OF/nE_SN(snow) " << ctrs.size()/NEx_OF_procc << ' ' << nE-Xdata.SoilNode << endl;
	
	Tm=wg*Tg+wi*Ti;
	gradTm=fvc::grad(Tm);
	Tm.correctBoundaryConditions();
	gradTm.correctBoundaryConditions();

    std::vector<double> OF_el_height(NEz_OF,0.0);
    std::vector<double> Tm_aveOverX(NEz_OF,0.0);
    std::vector<double> gradTm_aveOverX(NEz_OF,0.0);
    std::vector<double> rv_aveOverX(NEz_OF,0.0);
    std::vector<double> Jv_aveOverX(NEz_OF,0.0);
    std::vector<double> rhov_aveOverX(NEz_OF,0.0);
    std::vector<double> U_aveOverX(NEz_OF,0.0);
	for(label k=0; k<NEz_OF; k++)   
	{
		label cellI=k*NEx_OF_procc;
		OF_el_height[k]=ctrs[cellI].z();
		//
		scalar Tm_sum=0.0;
		vector gradTm_sum=Zero;
		scalar rv_sum=0.0;
		vector Jv_sum=Zero;
		scalar rhov_sum=0.0;
		scalar U_sum=0.0;
		//
		for(label i=0; i<NEx_OF_procc; i++)
		{
			label cI=cellI+i;
			//
			Tm_sum+=Tm[cI];
			gradTm_sum+=gradTm[cI];
			rv_sum+=rv[cI];
			Jv_sum+=Jv[cI];
			rhov_sum+=rho_v[cI];
			U_sum+=mag(U[cI]);
		}
		//
		reduce(Tm_sum, sumOp<scalar>()); /// Collect across all processors
		Tm_aveOverX[k]=Tm_sum/scalar(NEx_OF);		
		//
		reduce(gradTm_sum, sumOp<vector>()); /// Collect across all processors
		gradTm_aveOverX[k]=gradTm_sum.z()/scalar(NEx_OF);
		//
		reduce(rv_sum, sumOp<scalar>()); /// Collect across all processors
		rv_aveOverX[k]=rv_sum/scalar(NEx_OF);		
		//
		reduce(Jv_sum, sumOp<vector>()); /// Collect across all processors
		Jv_aveOverX[k]=Jv_sum.z()/scalar(NEx_OF);				
		//
		reduce(rhov_sum, sumOp<scalar>()); /// Collect across all processors
		rhov_aveOverX[k]=rhov_sum/scalar(NEx_OF);
		//
		//reduce(U_sum, sumOp<scalar>()); /// Collect across all processors
		U_aveOverX[k]=U_sum/scalar(NEx_OF);
				
	}
		           
    /// To be sure the first element in OF which is snow always gives the interpolation values for snow elements to SN
	/// we insert the elements using the iterator "begin()+the index of elements we need to add before"
	if(nE>Xdata.SoilNode) /// snow on the ground
	{
		OF_el_height.insert(OF_el_height.begin(),gMin(mesh.points()).z());
		Tm_aveOverX.insert(Tm_aveOverX.begin(),Tm_aveOverX[0]);
		gradTm_aveOverX.insert(gradTm_aveOverX.begin(),gradTm_aveOverX[0]);
		rv_aveOverX.insert(rv_aveOverX.begin(),rv_aveOverX[0]);
		Jv_aveOverX.insert(Jv_aveOverX.begin(),Jv_aveOverX[0]);
		rhov_aveOverX.insert(rhov_aveOverX.begin(),rhov_aveOverX[0]);		
		U_aveOverX.insert(U_aveOverX.begin(),U_aveOverX[0]);		
		//
		OF_el_height.insert(OF_el_height.begin()+OF_el_height.size(),gMax(mesh.points()).z());
		Tm_aveOverX.insert(Tm_aveOverX.begin()+Tm_aveOverX.size(),Tm_aveOverX[NEz_OF]);
		gradTm_aveOverX.insert(gradTm_aveOverX.begin()+gradTm_aveOverX.size(),gradTm_aveOverX[NEz_OF]);
		rv_aveOverX.insert(rv_aveOverX.begin()+rv_aveOverX.size(),rv_aveOverX[NEz_OF]);
		Jv_aveOverX.insert(Jv_aveOverX.begin()+Jv_aveOverX.size(),Jv_aveOverX[NEz_OF]);
		rhov_aveOverX.insert(rhov_aveOverX.begin()+rhov_aveOverX.size(),rhov_aveOverX[NEz_OF]);
		U_aveOverX.insert(U_aveOverX.begin()+U_aveOverX.size(),U_aveOverX[NEz_OF]);
	}	
	//
	for(label k=0; k<Xdata.SoilNode; k++)   
	{		
		//
		EMS[k].OF_to_SN_Tm=273.15;		
		EMS[k].OF_to_SN_gradTm=0.0;		
		EMS[k].OF_to_SN_Ug=0.0;		
		//
		EMS[k].rhov=0.0;
		EMS[k].vapTrans_snowDenChangeRate=0.0;
		EMS[k].vapTrans_fluxDiff=0.0;					
		EMS[k].vapTrans_underSaturationDegree=0.0;		
	}
	//
	for(label k=Xdata.SoilNode; k<nE; k++)   
	{
		scalar SN_el_height_=0.5*(NDS[k].z+NDS[k+1].z);		
		//
		EMS[k].OF_to_SN_Tm=solutio::LinearInterpolation(OF_el_height,Tm_aveOverX,SN_el_height_); //LogInterpolation // LinearInterpolation
		EMS[k].OF_to_SN_gradTm=solutio::LinearInterpolation(OF_el_height,gradTm_aveOverX,SN_el_height_);
		EMS[k].OF_to_SN_Ug=solutio::LinearInterpolation(OF_el_height,U_aveOverX,SN_el_height_);		
		//
		EMS[k].vapTrans_snowDenChangeRate=-solutio::LinearInterpolation(OF_el_height,rv_aveOverX,SN_el_height_); /// rv>0 means sublimation
		EMS[k].vapTrans_fluxDiff=solutio::LinearInterpolation(OF_el_height,Jv_aveOverX,SN_el_height_);		
		EMS[k].rhov=solutio::LinearInterpolation(OF_el_height,rhov_aveOverX,SN_el_height_);		
		scalar SN_el_rhovs=SnLaws::calc_rho_vs(EMS[k].OF_to_SN_Tm);
		EMS[k].vapTrans_underSaturationDegree=(EMS[k].rhov-SN_el_rhovs)/SN_el_rhovs;		
	}	
}

