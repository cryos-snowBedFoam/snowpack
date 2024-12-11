
package ProWin;


public interface C_DataFile

// if interface declared public, all members are public
// all members of interfaces are implicitly static and final
{
   /////////////////////////////////////////////////////////////////////////////
   // constants
   int ALL_IDCODES = -1;

   // enum DataLineType
   int DATE_TIME_DATA_LINE  = 1;
   int Y_VALUES_DATA_LINE   = 2;
   int Z_VALUES_DATA_LINE   = 3;
   int MET_VALUES_DATA_LINE = 4;

   // enum eDataIdCode
   int ID_CODE_NOT_SET = 0;
   int ID_CODE_AIR_TEMPERATURE = 10;       // Air Temperature
   int ID_CODE_TEMPERATURE1 = 40;
   int ID_CODE_TEMPERATURE2 = 42;
   int ID_CODE_TEMPERATURE3 = 44;
   int ID_CODE_TEMPERATURE4 = 46;
   int ID_CODE_TEMPERATURE5 = 48;

   int ID_CODE_DATE_TIME = 500;
   int ID_CODE_LAYER_HEIGHT = 501;         // Hoehe der Schneeschicht [cm]
   int ID_CODE_RHO = 502;                  // Dichte [kg/m\uFFFD]
   int ID_CODE_SNOWPACK_TEMPERATURE = 503; // Schneetemperatur  [\uFFFDC]
   int ID_CODE_SNOWPACK_TEMP_GRAD = 504;   // Schneetemperaturgradient [K/m]
   int ID_CODE_STRAIN_RATE = 505;          //
   int ID_CODE_WATER_CONTENT = 506;        // Wassergehalt [%]
   int ID_CODE_ICE_CONTENT = 507;          // Eisgehalt [%]
   int ID_CODE_DENDRICITY = 508;           //
   int ID_CODE_SPHERICITY = 509;           //
   int ID_CODE_COORDIN_NUM = 510;          //
   int ID_CODE_BOND_DIA = 511;             //
   int ID_CODE_GRAIN_DIA = 512;            // Korngroessen (Durchmesser) [mm]
   int ID_CODE_GRAIN_CLASS = 513;          // Kornformen
   // More ID_CODEs could be put here!!

   int MAX_NR_OF_LAYERS = 400;
   int MAX_DATA_LINE_LEN = 3000;
   int MAX_READ_ERRORS = 300000; // was 30;
   //int NR_OF_STATION_ID = 4;
}

