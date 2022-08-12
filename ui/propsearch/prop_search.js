
function addYears()
{
  // no db, slimy assumme 2000
  var e = document.getElementById("cycle");
  var fyr = new Date().getFullYear();
  fyr = (fyr-2000) + 3;
  if (e != null) {
    for (var ii=fyr;ii >= 0;ii--) {
      var oo = ("00" + ii).slice (-2)
      e.options[e.options.length] = new Option(oo,oo);
    }
  }
}



function addYearsOld()
{
  var xhttp;
  if ( window.XMLHttpRequest )
  {
    xhttp=new XMLHttpRequest();
  }
  else if (window.ActiveXObject )
  {
    xhttp = new ActiveXObject("Microsoft.XMLHTTP");
  };

  xhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
       var str = this.responseText; 
       var e = document.getElementById("cycle");
       if (e != null) {
           var arr = str.split(",");
           for (var ii=0;ii<arr.length ;ii++) {
             var oo = arr[ii];
             e.options[e.options.length] = new Option(oo,oo);
           }
      }
      else { alert("element is null"); }

    }
  };
  xhttp.open("GET", "/cgi-bin/propsearch/prop_get_ao.cgi", true);
  xhttp.send();

  return true;
}

function selectAll(x)
{
  var f = document.propResult;
  for (var ii=0; ii< f.length; ii++) {
    var e = f.elements[ii]
    if (e.type == "checkbox") {
      e.checked = x;
    }
  }
}

