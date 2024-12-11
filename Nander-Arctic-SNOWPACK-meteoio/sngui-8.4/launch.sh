#!/bin/sh
# To run the SNOWPACK Java visualisation

#initialize
pwdir=`pwd`
jarver=
snguidir=`dirname $0`
if [ ! -d "${snguidir}/SETUP" ]; then
	snguidir="${HOME}/alpine3d/sngui"
fi
setupfilepath="./SETUP/"
synletters=3
variant=dflt
usage="Usage: sn_gui [-h | --help] -s <number> [<variant>]"

exitus () {
	printf "%s\n" "Run SNOWPACK Java visualisation"
	printf "%s\n" "${usage}"
	printf "%s\n" "	Option(s):"
	printf "%s\n" "		-h | --help: display help"
	printf "%s\n" "		-s <number>: number of letters to synchronize files (>0)"
	printf "%s\n" "	Variants:"
	printf "\t\t%s\t%s\n" "dflt:" "Default"
	printf "\t\t%s\t%s\n" "ant:" "Antarctic"
	printf "\t\t%s\t%s\n" "Snat:" "Include visualization of Snat"
	printf "\t\t%s\t%s\n" "opera:" "Operational runs"
	printf "\t\t%s\t%s\n" "cal:" "Calibration"
	printf "\t%s\n" "Note: the path to the subdirectory where the PARDATA*.INI files reside will be adated in SETUP.INI according to <variant>"
	exit
}

################################################################################

# Read options
while [ $# -gt 0 ]; do
	par=$1
	shift
	case ${par} in
		-h|--help)
			exitus;;
		-*) pos=`expr length ${par}`
				while [ ${pos} -gt 1 ]; do
					option=$( echo ${par} | awk '{ str=substr($1, '$pos', 1); print str; }' )
					case ${option} in
						s)	synletters=$1
							shift;;
						?)	echo "No option -${option} available"
							exitus;;
					esac
					pos=$((pos-1))
				done;;
		*)	variant=${par}
			if [ $# -eq 0 ]; then
				break
			fi;;
	esac
done
if [ "${variant}" = "Snat" ]; then
	jarver="-${variant}"
fi
case ${variant} in
	ant|Snat|opera|cal)
		echo "Start ${variant}-variant of SN_GUI"
		setupfilepath="${setupfilepath}${variant}/";;
	dflt)
		echo "Start default variant of SN_GUI";;
	*)
		echo "No variant ${variant} of SN_GUI available!"
		exitus;;
esac

# adapt filepath in SETUP.INI
awk '
	/^IniFilePath/ {
		printf("IniFilePath = '${setupfilepath}'\n")
		next
	}
	/^SynLetters/ {
		printf("SynLetters = '${synletters}'\n")
		next
	}
	{
		print $0
	}
	' ${snguidir}/SETUP.INI > SETUP.tmp
mv -f SETUP.tmp ${snguidir}/SETUP.INI

# Start visualization
cd ${snguidir}
java -Xms32m -Xmx512m -jar bin/SN_GUI${jarver}.jar&
