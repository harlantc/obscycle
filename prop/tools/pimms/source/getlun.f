c _FORTRAN_INSERT_SAO_COPYRIGHT_HERE_(1997-2007)_
c _FORTRAN_INSERT_GPL_LICENSE_HERE_

*+GETLUN

        subroutine GETLUN( lun )

        implicit none

        integer lun

*       Returns a free unit number
*-GETLUN
        logical there

        lun = 11
        inquire( unit = lun, opened = there )
        do while( there )
          lun = lun + 1
          inquire( unit = lun, opened = there )
        end do

        end
