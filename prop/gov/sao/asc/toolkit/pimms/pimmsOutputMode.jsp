<table width="100%">
    <caption class="hdr">Output</caption>
  <tr>
     <td><input type="radio" 
	name="outputMode" value="mission" onClick="this.form.submit()"
	<%= ((! "flux".equals( session.getAttribute("outputMode"))) &&
	(! "density".equals(session.getAttribute("outputMode")))) ? "checked" : "" %>>&nbsp;<a href="prop_help_pimms.jsp#Count Rate">Count&nbsp;Rate</a>
    </td>
    <td><input type="radio" name="outputMode" value="flux"
               onClick="this.form.submit()"
	<%= "flux".equals( session.getAttribute("outputMode") ) ? "checked" : "" %>>&nbsp;<a href="prop_help_pimms.jsp#Flux">Flux</a>
    </td>
    <td><input type="radio" name="outputMode" value="density"
               onClick="this.form.submit()"
	<%= "density".equals( session.getAttribute("outputMode") ) ? "checked" : "" %>>&nbsp;<a href="prop_help_pimms.jsp#Density">Flux&nbsp;Density</a>
    </td>
  </tr>
</table>
