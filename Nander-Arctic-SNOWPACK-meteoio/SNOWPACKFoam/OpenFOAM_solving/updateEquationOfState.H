Info<< "updating the equation of state, rho, rho_a, rho_v, and psi....\n";

//new
rho= p*Mg/gaz_constant/Tg;
psi=Mg/gaz_constant/Tg;
rho_v= (rho_v/(rho_v+rho_a))*rho;
rho_a=rho-rho_v;
Mg=1.0/(rho_a/rho/Mair+rho_v/rho/Mvap);
