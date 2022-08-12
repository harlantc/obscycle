<table width="100%">
  <caption class="hdr" >Input </caption>
  <tr>
    <td><input type="radio" name="inputMode" value="mission" onClick="this.form.submit()"
	<%= ((! "flux".equals( session.getAttribute("inputMode"))) &&
	(! "density".equals(session.getAttribute("inputMode")))) ? "checked" : "" %>>&nbsp;<a href="prop_help_pimms.jsp#Count Rate">Count&nbsp;Rate</a>
    </td>
    <td><input type="radio" name="inputMode" value="flux"
               onClick="this.form.submit()"
	<%= "flux".equals( session.getAttribute("inputMode") ) ? "checked" : "" %>>&nbsp;<a href="prop_help_pimms.jsp#Flux">Flux</a>
    </td>
    <td><input type="radio" name="inputMode" value="density"
               onClick="this.form.submit()"
	<%= "density".equals( session.getAttribute("inputMode") ) ? "checked" : "" %>>&nbsp;<a href="prop_help_pimms.jsp#Density">Flux&nbsp;Density</a>
    </td>
  </tr>
</table>
