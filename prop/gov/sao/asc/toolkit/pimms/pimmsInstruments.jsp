<%
  String[] instrumentChoices = null;
  if (mission != null) 
     instrumentChoices = Toolkit.getInstrument(mission);

  for ( int i = 0; i < instrumentChoices.length; i++ ) {
%>
