
function  buildCCDs(ele)
{
    var sopts = new Array(
    ""," ",
    "Y","Y",
    "N","N",
    "O1","Off1",
    "O2","Off2",
    "O3","Off3",
    "O4","Off4",
    "O5","Off5"
    );
    for (var ii=0;ii<sopts.length;ii++) {
      ele.add(new Option(sopts[ii+1],sopts[ii]));
      ii++;
    }
}
function  buildChips(ele)
{
    var sopts = new Array(
    "","",
    "I0","I0",
    "I1","I1",
    "I2","I2",
    "I3","I3",
    "S0","S0",
    "S1","S1",
    "S2","S2",
    "S3","S3",
    "S4","S4",
    "S5","S5"
    );
    for (var ii=0;ii<sopts.length;ii++) {
      ele.put(sopts[ii],sopts[ii+1]);
      ii++;
    }
}

function  buildYN(ele,isGrid,phase=0)
{
    if (phase == 0){
      var sopts = new Array(
      "N","No constraint",
      "Y","Yes, required"
      );
    }
    // Alternate text for unique phase
    else{
      var sopts = new Array(
        "N","No, they can overlap",
        "Y","Yes, sample unique parts of phase window"
        );
    }
   if (isGrid == 1) {
     for (var ii=0;ii<sopts.length;ii++) {
       ele.put(sopts[ii],sopts[ii+1]);
       ii++;
     }
   } else {
    for (var ii=0;ii<sopts.length;ii++) {
      ele.add(new Option(sopts[ii+1],sopts[ii]));
      ii++;
    }
  }
}

function  buildConstraints(ele,isGrid)
{
    var sopts = new Array(
    "N","No constraint",
    "Y","Yes, required",
    "P","Preferred"
    );
   if (isGrid == 1) {
     for (var ii=0;ii<sopts.length;ii++) {
       ele.put(sopts[ii],sopts[ii+1]);
       ii++;
     }
   } else {
    for (var ii=0;ii<sopts.length;ii++) {
      ele.add(new Option(sopts[ii+1],sopts[ii]));
      ii++;
    }
  }
}

function  buildSubCat(ele)
{
    var sopts = new Array(
      "Solar System and Exoplanets",
      "Stars and WD",
      "WD Binaries and CV",
      "BH and NS Binaries",
      "SN, SNR and Isolated NS",
      "Gravitational Wave Event",
      "Normal Galaxies: Diffuse Emission",
      "Normal Galaxies: X-ray Populations",
      "Active Galaxies and Quasars",
      "Clusters of Galaxies",
      "Extragalactic Diffuse Emission and Surveys",
      "Galactic Diffuse Emission and Surveys"
    );
    ele.add(new Option("Please select a subject category",""));
    for (var ii=0;ii<sopts.length;ii++) {
      ele.add(new Option(sopts[ii],sopts[ii].toUpperCase()));
    }
}

function  buildPropType(ele,ddt)
{
    var sopts;
    if (ddt == "false") {
      sopts = new Array(
        "GO","General Observer (GO)",
        "LP","Large Project (LP)",
        "VLP","Very Large Project (VLP)",
        "TOO","Target of Opportunity (TOO)",
        "TOO/LP","LP with TOO",
        "ARCHIVE","Archive",
        "THEORY","Theory",
        "GTO","Guaranteed Time Observer (GTO)",
        "GTO/LP","LP from GTO",
        "GTO/TOO","TOO from GTO",
        "CAL","Calibration (Chandra personnel only)"
      );
    } else {
      sopts = new Array(
        "DDT","Director's Discretionary Time (DDT)",
        "CAL","Calibration (Chandra personnel only)"
      );
    }
    if (ele.length) {
      for (var ii=0;ii< ele.length;ii++) {
        ele.remove(ii);
      }
      ele.length = 0;
    }

    for (var ii=0;ii<sopts.length;ii++) {
      ele.add(new Option(sopts[ii+1],sopts[ii]));
      ii++;
    }
}

function  buildPropRights(ele,ddt)
{
    var sopts;
    if (ddt == "false") {
      sopts = new Array( " "," ",
        "S","Standard - 6 months",
        "4","4 months",
        "3","3 months",
        "2","2 months",
        "1","1 month",
        "N","No Proprietary Rights"
      );
    } else {
      sopts = new Array( " "," ",
        "D","Discretionary Rights 0-3 months",
        "N","No Proprietary Rights"
      );
    }
    if (ele.length) {
      for (var ii=0;ii< ele.length;ii++) {
        ele.remove(ii);
      }
      ele.length = 0;
    }
    for (var ii=0;ii<sopts.length;ii++) {
      ele.add(new Option(sopts[ii+1],sopts[ii]));
      ii++;
    }
    
}
