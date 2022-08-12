/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

C Apparent/mean place common block

c Time interval (yr) for proper motion correction
	REAL*8 PMT
C Barycentric position
	REAL*8 BPOS(3)
C Barycentric velocity
	REAL*8 BVEL(3)
C Heliocentric position
	REAL*8 HPOS(3)
C Light deflection
	REAL*8 GR2E
C Aberration constant
	REAL*8 AB
C Precession/nutation matrix
	REAL*8 PNM(3,3)
C
	COMMON /AMP/ PMT,BPOS,BVEL,HPOS,GR2E,AB,PNM
	SAVE   /AMP/

