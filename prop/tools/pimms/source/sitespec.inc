c _FORTRAN_INSERT_SAO_COPYRIGHT_HERE_(1997-2007)_
c _FORTRAN_INSERT_GPL_LICENSE_HERE_

* This is the Site Specific include file used by PIMMS
* Please change the directory specification below to suit the
* implementation at your site.

        character*256 ddir_name, mdir_name

* VAX/VMS examples
*        data ddir_name / '$disk1:[mukai.nra.pimms.develop.data]' /
*        data mdir_name / '$disk1:[mukai.nra.pimms.develop.models]' /

* UNIX examples
* If you have set the appropriate paths as values of these environment 
* variables then PIMMS will find your data and models directories.
* (Using continuation lines allows longer pathnames; Fortran still limits 
* the lengths of these paths to 64 characters.)

       data ddir_name / 
     &'$ASCDS_PROP_PMS_DATA'
     & /

       data mdir_name / 
     &'$ASCDS_PROP_PMS_MODEL'
     & /
