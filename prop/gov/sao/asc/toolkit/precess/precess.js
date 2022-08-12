// Simply submit the form.  This guarantees that the new form will
// uphold the precess requirements:
//
// 1) Only selection of systems of the form ...(J/Bxxxx) will supply
// an edit box to tailor the equinox,
//
// 2) A conversion of Ecliptic <--> Ecliptic is not allowed.
//
// The processing is done by the server.
//
// While this code is trival and could be placed in-line in the
// html/jsp file, it is placed here to maintain a JavaScipt
// infrastructure for future enhancements.

function processCoordinateSelection( form ) {
  form.submit();
}

function validateInput( form ) {
  // Validate input equinox, if present.

  // Validate output equinox if present.

  // Validate RA.

  // Validate DEC.

  // if any of the above are invalid, put them into a dialog box and
  // force the user to correct them before doing the submit.

  // Loop to: present old, errant values and help information; accept new
  // values; and check new values.


}
