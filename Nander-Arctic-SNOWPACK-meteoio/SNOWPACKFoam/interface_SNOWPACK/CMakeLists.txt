# Always set the cmake min version.
cmake_minimum_required(VERSION 3.0)

# Set our library version
set (PROJECT_VERSION "1.0")

# declarying our project
project(interface_SNOWPACK VERSION ${PROJECT_VERSION} DESCRIPTION "SNOWPACK interface for OpenFOAM")
set(CMAKE_BUILD_TYPE Release)

#For the shared library we need to use to build our shared library (.so):
set(PROJECT_LINK_LIBS libsnowpack.so libmeteoio.so)
link_directories( /usr/local/lib )

#Bring the headers, such as interface_SNOWPACK.H into the project
#Bring the headers, such as libsnowpack.h as an external library into the project
include_directories(include /usr/local/include/snowpack /usr/local/include/meteoio)

#IF(MSVC)
	#SET(getopt_src getopt.c getopt_long.c)
#ENDIF(MSVC)

#Can manually add the sources using the set command as follows:
#set(SOURCE_FILES src/interface_SNOWPACK.cpp)
#set(INCLUDE_FILES include/interface_SNOWPACK.hpp)
#However, the file(GLOB...) allows for wildcard additions:
FILE(GLOB_RECURSE INCLUDE_FILES "include/*.hpp")
FILE(GLOB_RECURSE SOURCE_FILES "src/*.cpp")


#This sets the location for library installation -- i.e., ${CMAKE_INSTALL_LIBDIR}=/usr/local/lib 
# and also the header for library if it has, in this case we do not have. ${CMAKE_INSTALL_INCLUDEDIR}=/usr/local/include
include(GNUInstallDirs)

#Generate the shared library from the sources
add_library(interface_SNOWPACK STATIC ${SOURCE_FILES})


# This will name your output .so files "libsomething.1.0" which is pretty useful
set_target_properties(interface_SNOWPACK PROPERTIES
    VERSION ${PROJECT_VERSION}
    SOVERSION 1
    PUBLIC_HEADER ${INCLUDE_FILES}
)


    
#Set the location for library installation -- i.e., /usr/lib in this case
# not really necessary in this example. Use "sudo make install" to apply
install(TARGETS interface_SNOWPACK
    DESTINATION  ${CMAKE_INSTALL_LIBDIR}
    PUBLIC_HEADER DESTINATION ${CMAKE_INSTALL_INCLUDEDIR}
    )

target_link_libraries(interface_SNOWPACK ${PROJECT_LINK_LIBS} )


ADD_CUSTOM_TARGET( distclean make clean
				   COMMAND ${CMAKE_COMMAND} -E remove build/*
				   COMMAND ${CMAKE_COMMAND} -E remove ${CMAKE_INSTALL_INCLUDEDIR}/interface_SNOWPACK.*   /usr/local/include/interface_SNOWPACK.*
				   COMMAND ${CMAKE_COMMAND} -E remove ${CMAKE_INSTALL_LIBDIR}/libinterface_SNOWPACK.*	/usr/local/lib/libinterface_SNOWPACK.*
					)

