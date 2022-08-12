*+NICER_LIMIT
        subroutine NICER_LIMIT( results, n_res )

        implicit none

        integer n_res_fixed
        real frac
        parameter( n_res_fixed = 2 )
        parameter( frac = 0.1 )

        integer n_res
        real results( 0: n_res )

*       Description:
*         Outputs NICER specific information to the screen via PWRITE
*
*       Arguments:
*         results (i)  : Predicted count rates
*         n_res   (i)  : Number of standard bands
*
*       Dependencies:
*         PCA_LIMIT_DO
*         PWRITE
*
*       Origin:
*         Created by KM, 2018 Aug, based on code for other missions
*         Updated 2019 Aug for Cycle 2
*
*       Author
*         Koji Mukai, 2018 Aug
*-NICER_LIMIT

	integer j
	real temp, work, fivesigma, syst, fivesig_b
        real bgd_rate( 0: n_res_fixed )
        character*10 bounds( n_res_fixed )
*        data bgd_rate / 6.379, 0.5800, 0.7513 /
        data bgd_rate / 2.44, 0.64, 0.55 /
        data bounds / ' 0.4-2 keV', '   2-8 keV' /
        character*77 xw_strng

        if( n_res .ne. n_res_fixed ) then
          call PWRITE( 'SEVERE ERROR:: PIMMS is confused in NCR_LIMIT' )
          return
        end if
        if( results( 0 ) .gt. 1.0e15 ) then
          xw_strng = 'Predicted count rate very high: mistake maybe?'
        else
          call PWRITE( ' ' )
          xw_strng = '%%% PIMMS predicts n.nnnE+mm cps from the source '
     &                                     // 'plus f.ff background cps'
          if( results( 0 ) .gt. 5000.0 ) then
            write( xw_strng( 20: 28 ), '(1p,e9.2)' ) results( 0 )
          else if( results( 0 ) .gt. 0.01 ) then
            write( xw_strng( 20: 28 ), '(f9.3)' ) results( 0 )
          else
            write( xw_strng( 20: 28 ), '(1p,e9.2)' ) results( 0 )
          end if
          write( xw_strng( 55: 58 ), '(f4.2)' ) bgd_rate( 0 )
          call PWRITE( xw_strng )
          xw_strng = '    in the standard (0.2-12 keV) band'
          call PWRITE( xw_strng )
          temp = results( 0 ) * results( 0 )
          if( temp .gt. 1e-30 ) then
            work = 25.0 * ( results( 0 ) + bgd_rate( 0 ) )
            fivesigma = work / temp
            xw_strng =
     &             '    5-sigma detection will be achieved in         s'
            if( fivesigma .gt. 5000.0 ) then
              write( xw_strng( 42: 50 ), '(1p,e9.2)' ) fivesigma
            else if( fivesigma .gt. 0.01 ) then
              write( xw_strng( 42: 50 ), '(f9.3)' ) fivesigma
            else
              write( xw_strng( 42: 50 ), '(1p,e9.2)' ) fivesigma
            end if
            call PWRITE( xw_strng )
            syst = frac * bgd_rate( 0 )
            if( results( 0 ) .gt. 5.0 * syst ) then
              fivesig_b = work / ( temp - 25.0 * syst * syst )
              xw_strng =
     &  '    (or in         s with 10% systematic uncertainties in bgd)'
              if( fivesig_b .gt. 5000.0 ) then
                write( xw_strng( 11: 19 ), '(1p,e9.2)' ) fivesig_b
              else if( fivesig_b .gt. 0.01 ) then
                write( xw_strng( 11: 19 ), '(f9.3)' ) fivesig_b
              else
                write( xw_strng( 11: 19 ), '(1p,e9.2)' ) fivesig_b
              end if
            else
              xw_strng =
     & '    (but undetectable with 10% systematic uncertainties in bgd)'
            end if
          else
            xw_strng =
     &              '    Count rate too low: 5-sigma detection unlikely'
          end if
          call PWRITE( xw_strng )
          call PWRITE( ' ' )
          call PWRITE( '%%% Results in the soft and hard bands are:' )
          call PWRITE( ' ' )
          call PWRITE(
     &                '     E range    Source  BGD  5-sigma    (+10%)' )
          call PWRITE(
     &                  '                 (cps) (cps)   detection (s)' )
          do j = 1, n_res_fixed
            xw_strng = ' '
            xw_strng( 4: 13 ) = bounds( j )
            xw_strng( 41: 51 ) = '(         )'
            if( results( j ) .gt. 5000.0 ) then
              write( xw_strng( 15: 23 ), '(1p,e9.2)' ) results( j )
            else if( results( j ) .gt. 0.01 ) then
              write( xw_strng( 15: 23 ), '(f9.3)' ) results( j )
            else
              write( xw_strng( 15: 23 ), '(1p,e9.2)' ) results( j )
            end if
            write( xw_strng( 25: 28 ), '(f4.2)' ) bgd_rate( j )
            if( results( j ) .gt. 1.0e15 ) then
              xw_strng( 29: 37 ) = ' ********'
              xw_strng( 40: 48 ) = '*********'
            else
              temp = results( j ) * results( j )
              if( temp .gt. 1e-30 ) then
                work = 25.0 * ( results( j ) + bgd_rate( j ) )
                fivesigma = work / temp
                if( fivesigma .gt. 5000.0 ) then
                  write( xw_strng( 29: 37 ), '(1p,e9.2)' ) fivesigma
                else if( fivesigma .gt. 0.01 ) then
                  write( xw_strng( 29: 37 ), '(f9.3)' ) fivesigma
                else
                  write( xw_strng( 29: 37 ), '(1p,e9.2)' ) fivesigma
                end if
                syst = frac * bgd_rate( j )
                if( results( j ) .gt. 5.0 * syst ) then
                  fivesig_b = work / ( temp - 25.0 * syst * syst )
                  if( fivesig_b .gt. 5000.0 ) then
                    write( xw_strng( 40: 48 ), '(1p,e9.2)' ) fivesig_b
                  else if( fivesig_b .gt. 0.01 ) then
                    write( xw_strng( 40: 48 ), '(f9.3)' ) fivesig_b
                  else
                    write( xw_strng( 40: 48 ), '(1p,e9.2)' ) fivesig_b
                  end if
                else
                  xw_strng( 40: 48 ) = '*********'
                end if
              else
                xw_strng( 29: 37 ) = ' ********'
                xw_strng( 40: 48 ) = '*********'
              end if
            end if
            call PWRITE( xw_strng )
          end do
        end if

        end
