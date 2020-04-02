system ( "if grep -oE \"errors\" test.json; then echo \"${component} doesnt exist\" && exit -1; fi" );
