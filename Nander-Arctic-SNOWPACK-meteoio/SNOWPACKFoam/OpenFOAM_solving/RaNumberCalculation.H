Info << ".....................Rayleigh number calculation " << endl;

dimensionedScalar keffs_averaged=keffs.weightedAverage(mesh.V());

dimensionedScalar K_averaged=K_.weightedAverage(mesh.V());

dimensionedScalar deltaT("deltaT", dimTemperature, NDS[Xdata.SoilNode].T-NDS[Xdata.getNumberOfNodes()-1].T);
Ra_= Foam::sqr(rho_a_ref)*Cp_a_ref*beta_a_ref*Foam::mag(gravity)*deltaT*H*K_/(mu_a_ref*keffs);

Ra_=Ra_.weightedAverage(mesh.V());


dimensionedScalar Ra_ave_1 = Ra_.weightedAverage(mesh.V());
dimensionedScalar Ra_ave_2 = Foam::sqr(rho_a_ref)*Cp_a_ref*beta_a_ref*Foam::mag(gravity)*deltaT*H*K_averaged/(mu_a_ref*keffs_averaged);
Info<< "Rayleigh number averaged 1 based on H=1 and over Ra_ field: " << Ra_ave_1.value() << endl;
Info<< "Rayleigh number averaged 2 based on H=1, K_ave, and keff_ave  " << Ra_ave_2.value() << endl;
