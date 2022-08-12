#   ******************************************************************
#   Copyright (c) 1999, 2000   Smithsonian  Astrophysical  Observatory
#   The contents of this file are protected under the copyright notice
#   contained in the file  COPYRIGHT_SAO located in the root directory
#   of this distribution.
#   ******************************************************************

#
# Makefile for data subdirectory of pimms
#
# 10/18/2016 - add 26 of ao19 ; replace 13 of astroh w/ hitomi ; remove 12 ixo_*.area ;
# 

MK_TOP = ../../../../..

include $(MK_TOP)/infra/Makefile.prop


PIMMS_DATA = nicer__.area          hitomi_sgd_.special \
             asca_gis_.area        asca_gis_.special \
             asca_sis_.area        asca_sis_.special     asca_sis_psf.dat \
             bbxrt_a0_.area \
             cgro_osse_.area \
pms_mssn.lst      pms_banner.txt \
hitomi_hxi_all.area   hitomi_hxi_top.area    hitomi_sgd_.area    \
hitomi_sxi_.area      hitomi_sxs_be25.area   hitomi_sxs_be50.area    \
hitomi_sxs_obf.area   hitomi_sxs_open.area   \
integral_isgri_.area  integral_jemx_.area                \
nustar__.area  \
hitomi_sxs_be25.special     hitomi_sxs_be50.special      hitomi_sxs_obf.special  \
hitomi_sxs_open.special     integral_jemx_.special       nustar__.special  \
heao1_a1_agcl.area    heao1_a1_agcp.area    maxi_gsc_.area    maxi_ssc_.area \
heao1_a1_agcl.special heao1_a1_agcp.special maxi_gsc_.special maxi_ssc_.special \
xmm_om_b.area  xmm_om_u.area  xmm_om_uvm2.area  xmm_om_uvw1.area  xmm_om_uvw2.area  xmm_om_v.area  nicer__.special \
athena_xifu_thin.area      athena_xifu_thick.area      athena_xifu_be100.area      athena_xifu_baseline.area \
athena_xifu_thin.special   athena_xifu_thick.special   athena_xifu_be100.special   athena_xifu_baseline.special \
chandra-ao22_letg-hrc-s_letg1.special    chandra-ao22_letg-acis-s_order0.special   chandra-ao22_letg-acis-s_letg1.special \
chandra-ao22_letg-acis-i_order0.special  chandra-ao22_hetg-acis-s_order0.special   chandra-ao22_hetg-acis-s_meg1.special  \
chandra-ao22_hetg-acis-s_heg1.special    chandra-ao22_hetg-acis-s_heg1meg1.special chandra-ao22_hetg-acis-i_order0.special \
chandra-ao22_acis-s_.special             chandra-ao22_acis-i_.special              chandra-ao22_letg-hrc-s_order0.area \
chandra-ao22_letg-hrc-s_letghi.area      chandra-ao22_letg-hrc-s_letg1.area        chandra-ao22_letg-hrc-i_order0.area \
chandra-ao22_letg-acis-s_order0.area     chandra-ao22_letg-acis-s_letg1.area       chandra-ao22_letg-acis-i_order0.area \
chandra-ao22_hrc-s_.area                 chandra-ao22_hrc-i_.area                  chandra-ao22_hetg-acis-s_order0.area  \
chandra-ao22_hetg-acis-s_meg1.area       chandra-ao22_hetg-acis-s_heg1meg1.area    chandra-ao22_hetg-acis-s_heg1.area \
chandra-ao22_hetg-acis-i_order0.area     chandra-ao22_acis-s_.area                 chandra-ao22_acis-i_.area \
chandra-ao21_letg-hrc-s_letg1.special    chandra-ao21_letg-acis-s_order0.special   chandra-ao21_letg-acis-s_letg1.special \
chandra-ao21_letg-acis-i_order0.special  chandra-ao21_hetg-acis-s_order0.special   chandra-ao21_hetg-acis-s_meg1.special  \
chandra-ao21_hetg-acis-s_heg1.special    chandra-ao21_hetg-acis-s_heg1meg1.special chandra-ao21_hetg-acis-i_order0.special \
chandra-ao21_acis-s_.special             chandra-ao21_acis-i_.special              chandra-ao21_letg-hrc-s_order0.area \
chandra-ao21_letg-hrc-s_letghi.area      chandra-ao21_letg-hrc-s_letg1.area        chandra-ao21_letg-hrc-i_order0.area \
chandra-ao21_letg-acis-s_order0.area     chandra-ao21_letg-acis-s_letg1.area       chandra-ao21_letg-acis-i_order0.area \
chandra-ao21_hrc-s_.area                 chandra-ao21_hrc-i_.area                  chandra-ao21_hetg-acis-s_order0.area  \
chandra-ao21_hetg-acis-s_meg1.area       chandra-ao21_hetg-acis-s_heg1meg1.area    chandra-ao21_hetg-acis-s_heg1.area \
chandra-ao21_hetg-acis-i_order0.area     chandra-ao21_acis-s_.area                 chandra-ao21_acis-i_.area \
chandra-ao20_letg-hrc-s_letg1.special      chandra-ao20_letg-acis-s_order0.special \
chandra-ao20_letg-acis-s_letg1.special     chandra-ao20_letg-acis-i_order0.special \
chandra-ao20_hetg-acis-s_order0.special    chandra-ao20_hetg-acis-s_meg1.special  \
chandra-ao20_hetg-acis-s_heg1.special      chandra-ao20_hetg-acis-s_heg1meg1.special \
chandra-ao20_hetg-acis-i_order0.special    chandra-ao20_acis-s_.special \
chandra-ao20_acis-i_.special     \
chandra-ao20_letg-hrc-s_order0.area        chandra-ao20_letg-hrc-s_letghi.area \
chandra-ao20_letg-hrc-s_letg1.area         chandra-ao20_letg-hrc-i_order0.area  \
chandra-ao20_letg-acis-s_order0.area       chandra-ao20_letg-acis-s_letg1.area  \
chandra-ao20_letg-acis-i_order0.area       chandra-ao20_hrc-s_.area \
chandra-ao20_hrc-i_.area                   chandra-ao20_hetg-acis-s_order0.area \
chandra-ao20_hetg-acis-s_meg1.area         chandra-ao20_hetg-acis-s_heg1meg1.area \
chandra-ao20_hetg-acis-s_heg1.area         chandra-ao20_hetg-acis-i_order0.area \
chandra-ao20_acis-s_.area                  chandra-ao20_acis-i_.area \
chandra-ao19_letg-hrc-s_letg1.special      chandra-ao19_letg-acis-s_order0.special \
chandra-ao19_letg-acis-s_letg1.special     chandra-ao19_letg-acis-i_order0.special \
chandra-ao19_hetg-acis-s_order0.special    chandra-ao19_hetg-acis-s_meg1.special  \
chandra-ao19_hetg-acis-s_heg1.special      chandra-ao19_hetg-acis-s_heg1meg1.special \
chandra-ao19_hetg-acis-i_order0.special    chandra-ao19_acis-s_.special \
chandra-ao19_acis-i_.special     \
chandra-ao19_letg-hrc-s_order0.area        chandra-ao19_letg-hrc-s_letghi.area \
chandra-ao19_letg-hrc-s_letg1.area         chandra-ao19_letg-hrc-i_order0.area  \
chandra-ao19_letg-acis-s_order0.area       chandra-ao19_letg-acis-s_letg1.area  \
chandra-ao19_letg-acis-i_order0.area       chandra-ao19_hrc-s_.area \
chandra-ao19_hrc-i_.area                   chandra-ao19_hetg-acis-s_order0.area \
chandra-ao19_hetg-acis-s_meg1.area         chandra-ao19_hetg-acis-s_heg1meg1.area \
chandra-ao19_hetg-acis-s_heg1.area         chandra-ao19_hetg-acis-i_order0.area \
chandra-ao19_acis-s_.area                  chandra-ao19_acis-i_.area \
chandra-ao18_letg-hrc-s_letg1.special      chandra-ao18_letg-acis-s_order0.special \
chandra-ao18_letg-acis-s_letg1.special     chandra-ao18_letg-acis-i_order0.special \
chandra-ao18_hetg-acis-s_order0.special    chandra-ao18_hetg-acis-s_meg1.special  \
chandra-ao18_hetg-acis-s_heg1.special      chandra-ao18_hetg-acis-s_heg1meg1.special \
chandra-ao18_hetg-acis-i_order0.special    chandra-ao18_acis-s_.special \
chandra-ao18_acis-i_.special     \
chandra-ao18_letg-hrc-s_order0.area        chandra-ao18_letg-hrc-s_letghi.area \
chandra-ao18_letg-hrc-s_letg1.area         chandra-ao18_letg-hrc-i_order0.area  \
chandra-ao18_letg-acis-s_order0.area       chandra-ao18_letg-acis-s_letg1.area  \
chandra-ao18_letg-acis-i_order0.area       chandra-ao18_hrc-s_.area \
chandra-ao18_hrc-i_.area                   chandra-ao18_hetg-acis-s_order0.area \
chandra-ao18_hetg-acis-s_meg1.area         chandra-ao18_hetg-acis-s_heg1meg1.area \
chandra-ao18_hetg-acis-s_heg1.area         chandra-ao18_hetg-acis-i_order0.area \
chandra-ao18_acis-s_.area                  chandra-ao18_acis-i_.area \
chandra-ao17_letg-hrc-s_letg1.special      chandra-ao17_letg-acis-s_order0.special \
chandra-ao17_letg-acis-s_letg1.special     chandra-ao17_letg-acis-i_order0.special \
chandra-ao17_hetg-acis-s_order0.special    chandra-ao17_hetg-acis-s_meg1.special  \
chandra-ao17_hetg-acis-s_heg1.special      chandra-ao17_hetg-acis-s_heg1meg1.special \
chandra-ao17_hetg-acis-i_order0.special    chandra-ao17_acis-s_.special \
chandra-ao17_acis-i_.special     \
chandra-ao17_letg-hrc-s_order0.area        chandra-ao17_letg-hrc-s_letghi.area \
chandra-ao17_letg-hrc-s_letg1.area         chandra-ao17_letg-hrc-i_order0.area  \
chandra-ao17_letg-acis-s_order0.area       chandra-ao17_letg-acis-s_letg1.area  \
chandra-ao17_letg-acis-i_order0.area       chandra-ao17_hrc-s_.area \
chandra-ao17_hrc-i_.area                   chandra-ao17_hetg-acis-s_order0.area \
chandra-ao17_hetg-acis-s_meg1.area         chandra-ao17_hetg-acis-s_heg1meg1.area \
chandra-ao17_hetg-acis-s_heg1.area         chandra-ao17_hetg-acis-i_order0.area \
chandra-ao17_acis-s_.area                  chandra-ao17_acis-i_.area \
chandra-ao16_letg-hrc-s_letg1.special      chandra-ao16_letg-acis-s_order0.special \
chandra-ao16_letg-acis-s_letg1.special     chandra-ao16_letg-acis-i_order0.special \
chandra-ao16_hetg-acis-s_order0.special    chandra-ao16_hetg-acis-s_meg1.special  \
chandra-ao16_hetg-acis-s_heg1.special      chandra-ao16_hetg-acis-s_heg1meg1.special \
chandra-ao16_hetg-acis-i_order0.special    chandra-ao16_acis-s_.special \
chandra-ao16_acis-i_.special     \
chandra-ao16_letg-hrc-s_order0.area        chandra-ao16_letg-hrc-s_letghi.area \
chandra-ao16_letg-hrc-s_letg1.area         chandra-ao16_letg-hrc-i_order0.area  \
chandra-ao16_letg-acis-s_order0.area       chandra-ao16_letg-acis-s_letg1.area  \
chandra-ao16_letg-acis-i_order0.area       chandra-ao16_hrc-s_.area \
chandra-ao16_hrc-i_.area                   chandra-ao16_hetg-acis-s_order0.area \
chandra-ao16_hetg-acis-s_meg1.area         chandra-ao16_hetg-acis-s_heg1meg1.area \
chandra-ao16_hetg-acis-s_heg1.area         chandra-ao16_hetg-acis-i_order0.area \
chandra-ao16_acis-s_.area                  chandra-ao16_acis-i_.area \
chandra-ao15_letg-hrc-s_letg1.special      chandra-ao15_letg-acis-s_order0.special \
chandra-ao15_letg-acis-s_letg1.special     chandra-ao15_letg-acis-i_order0.special \
chandra-ao15_hetg-acis-s_order0.special    chandra-ao15_hetg-acis-s_meg1.special  \
chandra-ao15_hetg-acis-s_heg1.special      chandra-ao15_hetg-acis-s_heg1meg1.special \
chandra-ao15_hetg-acis-i_order0.special    chandra-ao15_acis-s_.special \
chandra-ao15_acis-i_.special     \
chandra-ao15_letg-hrc-s_order0.area        chandra-ao15_letg-hrc-s_letghi.area \
chandra-ao15_letg-hrc-s_letg1.area         chandra-ao15_letg-hrc-i_order0.area  \
chandra-ao15_letg-acis-s_order0.area       chandra-ao15_letg-acis-s_letg1.area  \
chandra-ao15_letg-acis-i_order0.area       chandra-ao15_hrc-s_.area \
chandra-ao15_hrc-i_.area                   chandra-ao15_hetg-acis-s_order0.area \
chandra-ao15_hetg-acis-s_meg1.area         chandra-ao15_hetg-acis-s_heg1meg1.area \
chandra-ao15_hetg-acis-s_heg1.area         chandra-ao15_hetg-acis-i_order0.area \
chandra-ao15_acis-s_.area                  chandra-ao15_acis-i_.area \
chandra-ao14_letg-hrc-s_letg1.special      chandra-ao14_letg-acis-s_order0.special \
chandra-ao14_letg-acis-s_letg1.special     chandra-ao14_letg-acis-i_order0.special \
chandra-ao14_hetg-acis-s_order0.special    chandra-ao14_hetg-acis-s_meg1.special  \
chandra-ao14_hetg-acis-s_heg1.special      chandra-ao14_hetg-acis-s_heg1meg1.special \
chandra-ao14_hetg-acis-i_order0.special    chandra-ao14_acis-s_.special \
chandra-ao14_acis-i_.special     \
chandra-ao14_letg-hrc-s_order0.area        chandra-ao14_letg-hrc-s_letghi.area \
chandra-ao14_letg-hrc-s_letg1.area         chandra-ao14_letg-hrc-i_order0.area  \
chandra-ao14_letg-acis-s_order0.area       chandra-ao14_letg-acis-s_letg1.area  \
chandra-ao14_letg-acis-i_order0.area       chandra-ao14_hrc-s_.area \
chandra-ao14_hrc-i_.area                   chandra-ao14_hetg-acis-s_order0.area \
chandra-ao14_hetg-acis-s_meg1.area         chandra-ao14_hetg-acis-s_heg1meg1.area \
chandra-ao14_hetg-acis-s_heg1.area         chandra-ao14_hetg-acis-i_order0.area \
chandra-ao14_acis-s_.area                  chandra-ao14_acis-i_.area  \
chandra-ao13_letg-hrc-s_letg1.special      chandra-ao13_letg-acis-s_order0.special \
chandra-ao13_letg-acis-s_letg1.special     chandra-ao13_letg-acis-i_order0.special \
chandra-ao13_hetg-acis-s_order0.special    chandra-ao13_hetg-acis-s_meg1.special  \
chandra-ao13_hetg-acis-s_heg1.special      chandra-ao13_hetg-acis-s_heg1meg1.special \
chandra-ao13_hetg-acis-i_order0.special    chandra-ao13_acis-s_.special \
chandra-ao13_acis-i_.special     \
chandra-ao13_letg-hrc-s_order0.area        chandra-ao13_letg-hrc-s_letghi.area \
chandra-ao13_letg-hrc-s_letg1.area         chandra-ao13_letg-hrc-i_order0.area  \
chandra-ao13_letg-acis-s_order0.area       chandra-ao13_letg-acis-s_letg1.area  \
chandra-ao13_letg-acis-i_order0.area       chandra-ao13_hrc-s_.area \
chandra-ao13_hrc-i_.area                   chandra-ao13_hetg-acis-s_order0.area \
chandra-ao13_hetg-acis-s_meg1.area         chandra-ao13_hetg-acis-s_heg1meg1.area \
chandra-ao13_hetg-acis-s_heg1.area         chandra-ao13_hetg-acis-i_order0.area \
chandra-ao13_acis-s_.area                  chandra-ao13_acis-i_.area  \
chandra-ao12_letg-hrc-s_letg1.special      chandra-ao12_letg-acis-s_order0.special \
chandra-ao12_letg-acis-s_letg1.special     chandra-ao12_letg-acis-i_order0.special \
chandra-ao12_hetg-acis-s_order0.special    chandra-ao12_hetg-acis-s_meg1.special \
chandra-ao12_hetg-acis-s_heg1.special      chandra-ao12_hetg-acis-s_heg1meg1.special \
chandra-ao12_hetg-acis-i_order0.special    chandra-ao12_acis-s_.special \
chandra-ao12_acis-i_.special \
chandra-ao12_letg-hrc-s_order0.area        chandra-ao12_letg-hrc-s_letghi.area \
chandra-ao12_letg-hrc-s_letg1.area         chandra-ao12_letg-hrc-i_order0.area \
chandra-ao12_letg-acis-s_order0.area       chandra-ao12_letg-acis-s_letg1.area \
chandra-ao12_letg-acis-i_order0.area       chandra-ao12_hrc-s_.area \
chandra-ao12_hrc-i_.area                   chandra-ao12_hetg-acis-s_order0.area \
chandra-ao12_hetg-acis-s_meg1.area         chandra-ao12_hetg-acis-s_heg1meg1.area \
chandra-ao12_hetg-acis-s_heg1.area         chandra-ao12_hetg-acis-i_order0.area \
chandra-ao12_acis-s_.area                  chandra-ao12_acis-i_.area \
chandra-ao11_letg-hrc-s_letg1.special      chandra-ao11_letg-acis-s_order0.special \
chandra-ao11_letg-acis-s_letg1.special     chandra-ao11_letg-acis-i_order0.special \
chandra-ao11_hetg-acis-s_order0.special    chandra-ao11_hetg-acis-s_meg1.special \
chandra-ao11_hetg-acis-s_heg1.special      chandra-ao11_hetg-acis-s_heg1meg1.special \
chandra-ao11_hetg-acis-i_order0.special    chandra-ao11_acis-s_.special \
chandra-ao11_acis-i_.special \
chandra-ao11_letg-hrc-s_order0.area        chandra-ao11_letg-hrc-s_letghi.area \
chandra-ao11_letg-hrc-s_letg1.area         chandra-ao11_letg-hrc-i_order0.area \
chandra-ao11_letg-acis-s_order0.area       chandra-ao11_letg-acis-s_letg1.area \
chandra-ao11_letg-acis-i_order0.area       chandra-ao11_hrc-s_.area \
chandra-ao11_hrc-i_.area                   chandra-ao11_hetg-acis-s_order0.area \
chandra-ao11_hetg-acis-s_meg1.area         chandra-ao11_hetg-acis-s_heg1meg1.area \
chandra-ao11_hetg-acis-s_heg1.area         chandra-ao11_hetg-acis-i_order0.area \
chandra-ao11_acis-s_.area                  chandra-ao11_acis-i_.area \
chandra-ao10_letg-hrc-s_letg1.special      chandra-ao10_letg-acis-s_order0.special \
chandra-ao10_letg-acis-s_letg1.special     chandra-ao10_letg-acis-i_order0.special \
chandra-ao10_hetg-acis-s_order0.special    chandra-ao10_hetg-acis-s_meg1.special \
chandra-ao10_hetg-acis-s_heg1.speciali     chandra-ao10_hetg-acis-s_heg1meg1.special \
chandra-ao10_hetg-acis-i_order0.special    chandra-ao10_acis-s_.special \
chandra-ao10_acis-i_.special \
chandra-ao10_letg-hrc-s_order0.area        chandra-ao10_letg-hrc-s_letghi.area \
chandra-ao10_letg-hrc-s_letg1.area         chandra-ao10_letg-hrc-i_order0.area \
chandra-ao10_letg-acis-s_order0.area       chandra-ao10_letg-acis-s_letg1.area \
chandra-ao10_letg-acis-i_order0.area       chandra-ao10_hrc-s_.area \
chandra-ao10_hrc-i_.area                   chandra-ao10_hetg-acis-s_order0.area \
chandra-ao10_hetg-acis-s_meg1.area         chandra-ao10_hetg-acis-s_heg1meg1.area \
chandra-ao10_hetg-acis-s_heg1.area         chandra-ao10_hetg-acis-i_order0.area \
chandra-ao10_acis-s_.area                  chandra-ao10_acis-i_.area \
chandra-ao9_acis-i_.special                chandra-ao9_acis-s_.special \
chandra-ao9_hetg-acis-s_order0.special     chandra-ao9_hetg-acis-i_order0.special \
chandra-ao9_letg-acis-s_order0.special     chandra-ao9_letg-acis-i_order0.special \
chandra-ao9_hetg-acis-s_heg1.special       chandra-ao9_hetg-acis-s_heg1meg1.special \
chandra-ao9_hetg-acis-s_meg1.special       chandra-ao9_letg-acis-s_letg1.special \
chandra-ao9_letg-hrc-s_letg1.special \
chandra-ao9_acis-i_.area                   chandra-ao9_acis-s_.area \
chandra-ao9_hetg-acis-i_order0.area        chandra-ao9_hetg-acis-s_heg1.area \
chandra-ao9_hetg-acis-s_heg1meg1.area      chandra-ao9_hetg-acis-s_meg1.area \
chandra-ao9_hetg-acis-s_order0.area        chandra-ao9_hrc-i_.area \
chandra-ao9_hrc-s_.area                    chandra-ao9_letg-acis-i_order0.area \
chandra-ao9_letg-acis-s_letg1.area         chandra-ao9_letg-acis-s_order0.area \
chandra-ao9_letg-hrc-i_order0.area         chandra-ao9_letg-hrc-s_letg1.area \
chandra-ao9_letg-hrc-s_letghi.area         chandra-ao9_letg-hrc-s_order0.area \
chandra-ao8_acis-i_.special                chandra-ao8_acis-s_.special \
chandra-ao8_hetg-acis-s_order0.special     chandra-ao8_hetg-acis-i_order0.special \
chandra-ao8_letg-acis-s_order0.special     chandra-ao8_letg-acis-i_order0.special \
chandra-ao8_hetg-acis-s_heg1.special       chandra-ao8_hetg-acis-s_heg1meg1.special \
chandra-ao8_hetg-acis-s_meg1.special       chandra-ao8_letg-acis-s_letg1.special \
chandra-ao8_letg-hrc-s_letg1.special \
chandra-ao8_acis-i_.area                   chandra-ao8_acis-s_.area \
chandra-ao8_hetg-acis-i_order0.area        chandra-ao8_hetg-acis-s_heg1.area \
chandra-ao8_hetg-acis-s_heg1meg1.area      chandra-ao8_hetg-acis-s_meg1.area \
chandra-ao8_hetg-acis-s_order0.area        chandra-ao8_hrc-i_.area \
chandra-ao8_hrc-s_.area                    chandra-ao8_letg-acis-i_order0.area \
chandra-ao8_letg-acis-s_letg1.area         chandra-ao8_letg-acis-s_order0.area \
chandra-ao8_letg-hrc-i_order0.area         chandra-ao8_letg-hrc-s_letg1.area \
chandra-ao8_letg-hrc-s_letghi.area         chandra-ao8_letg-hrc-s_order0.area \
chandra-ao7_acis-i_.special                chandra-ao7_acis-s_.special \
chandra-ao7_hetg-acis-s_order0.special     chandra-ao7_hetg-acis-i_order0.special \
chandra-ao7_letg-acis-s_order0.special     chandra-ao7_letg-acis-i_order0.special \
chandra-ao7_hetg-acis-s_heg1.special       chandra-ao7_hetg-acis-s_heg1meg1.special \
chandra-ao7_hetg-acis-s_meg1.special       chandra-ao7_letg-acis-s_letg1.special \
chandra-ao7_letg-hrc-s_letg1.special \
chandra-ao7_acis-i_.area \
chandra-ao7_acis-s_.area \
chandra-ao7_hetg-acis-i_order0.area \
chandra-ao7_hetg-acis-s_heg1.area \
chandra-ao7_hetg-acis-s_heg1meg1.area \
chandra-ao7_hetg-acis-s_meg1.area \
chandra-ao7_hetg-acis-s_order0.area \
chandra-ao7_hrc-i_.area \
chandra-ao7_hrc-s_.area \
chandra-ao7_letg-acis-i_order0.area \
chandra-ao7_letg-acis-s_letg1.area \
chandra-ao7_letg-acis-s_order0.area \
chandra-ao7_letg-hrc-i_order0.area \
chandra-ao7_letg-hrc-s_letg1.area \
chandra-ao7_letg-hrc-s_letghi.area \
chandra-ao7_letg-hrc-s_order0.area \
chandra-ao6_acis-i_.special \
chandra-ao6_acis-s_.special \
chandra-ao6_hetg-acis-s_order0.special \
chandra-ao6_hetg-acis-i_order0.special \
chandra-ao6_letg-acis-s_order0.special \
chandra-ao6_letg-acis-i_order0.special \
chandra-ao6_hetg-acis-s_heg1.special \
chandra-ao6_hetg-acis-s_heg1meg1.special \
chandra-ao6_hetg-acis-s_meg1.special \
chandra-ao6_letg-acis-s_letg1.special \
chandra-ao6_letg-hrc-s_letg1.special \
chandra-ao6_acis-i_.area \
chandra-ao6_acis-s_.area \
chandra-ao6_hetg-acis-i_order0.area \
chandra-ao6_hetg-acis-s_heg1.area \
chandra-ao6_hetg-acis-s_heg1meg1.area \
chandra-ao6_hetg-acis-s_meg1.area \
chandra-ao6_hetg-acis-s_order0.area \
chandra-ao6_hrc-i_.area \
chandra-ao6_hrc-s_.area \
chandra-ao6_letg-acis-i_order0.area \
chandra-ao6_letg-acis-s_letg1.area \
chandra-ao6_letg-acis-s_order0.area \
chandra-ao6_letg-hrc-i_order0.area \
chandra-ao6_letg-hrc-s_letg1.area \
chandra-ao6_letg-hrc-s_letghi.area \
chandra-ao6_letg-hrc-s_order0.area \
chandra-ao5_acis-i_.special \
chandra-ao5_acis-s_.special \
chandra-ao5_hetg-acis-s_order0.special \
chandra-ao5_hetg-acis-i_order0.special \
chandra-ao5_letg-acis-s_order0.special \
chandra-ao5_letg-acis-i_order0.special \
chandra-ao5_hetg-acis-s_heg1.special \
chandra-ao5_hetg-acis-s_heg1meg1.special \
chandra-ao5_hetg-acis-s_meg1.special \
chandra-ao5_letg-acis-s_letg1.special \
chandra-ao5_letg-hrc-s_letg1.special \
chandra-ao5_acis-i_.area \
chandra-ao5_acis-s_.area \
chandra-ao5_hetg-acis-i_order0.area \
chandra-ao5_hetg-acis-s_heg1.area \
chandra-ao5_hetg-acis-s_heg1meg1.area \
chandra-ao5_hetg-acis-s_meg1.area \
chandra-ao5_hetg-acis-s_order0.area \
chandra-ao5_hrc-i_.area \
chandra-ao5_hrc-s_.area \
chandra-ao5_letg-acis-i_order0.area \
chandra-ao5_letg-acis-s_letg1.area \
chandra-ao5_letg-acis-s_order0.area \
chandra-ao5_letg-hrc-i_order0.area \
chandra-ao5_letg-hrc-s_letg1.area \
chandra-ao5_letg-hrc-s_letghi.area \
chandra-ao5_letg-hrc-s_order0.area \
chandra-ao4_acis-i_.special \
chandra-ao4_acis-s_.special \
chandra-ao4_hetg-acis-s_order0.special \
chandra-ao4_hetg-acis-i_order0.special \
chandra-ao4_letg-acis-s_order0.special \
chandra-ao4_letg-acis-i_order0.special \
chandra-ao4_hetg-acis-s_heg1.special \
chandra-ao4_hetg-acis-s_heg1meg1.special \
chandra-ao4_hetg-acis-s_meg1.special \
chandra-ao4_letg-acis-s_letg1.special \
chandra-ao4_letg-hrc-s_letg1.special \
chandra-ao4_acis-i_.area \
chandra-ao4_acis-s_.area \
chandra-ao4_hetg-acis-i_order0.area \
chandra-ao4_hetg-acis-s_heg1.area \
chandra-ao4_hetg-acis-s_heg1meg1.area \
chandra-ao4_hetg-acis-s_meg1.area \
chandra-ao4_hetg-acis-s_order0.area \
chandra-ao4_hrc-i_.area \
chandra-ao4_hrc-s_.area \
chandra-ao4_letg-acis-i_order0.area \
chandra-ao4_letg-acis-s_letg1.area \
chandra-ao4_letg-acis-s_order0.area \
chandra-ao4_letg-hrc-i_order0.area \
chandra-ao4_letg-hrc-s_letg1.area \
chandra-ao4_letg-hrc-s_letghi.area \
chandra-ao4_letg-hrc-s_order0.area \
chandra-ao3_acis-i_.special \
chandra-ao3_acis-s_.special \
chandra-ao3_hetg-acis-s_order0.special \
chandra-ao3_hetg-acis-i_order0.special \
chandra-ao3_letg-acis-s_order0.special \
chandra-ao3_letg-acis-i_order0.special \
chandra-ao3_hetg-acis-s_heg1.special \
chandra-ao3_hetg-acis-s_heg1meg1.special \
chandra-ao3_hetg-acis-s_meg1.special \
chandra-ao3_letg-acis-s_letg1.special \
chandra-ao3_letg-hrc-s_letg1.special \
chandra-ao3_acis-i_.area \
chandra-ao3_acis-s_.area \
chandra-ao3_hetg-acis-i_order0.area \
chandra-ao3_hetg-acis-s_heg1.area \
chandra-ao3_hetg-acis-s_heg1meg1.area \
chandra-ao3_hetg-acis-s_meg1.area \
chandra-ao3_hetg-acis-s_order0.area \
chandra-ao3_hrc-i_.area \
chandra-ao3_hrc-s_.area \
chandra-ao3_letg-acis-i_order0.area \
chandra-ao3_letg-acis-s_letg1.area \
chandra-ao3_letg-acis-s_order0.area \
chandra-ao3_letg-hrc-i_order0.area \
chandra-ao3_letg-hrc-s_letg1.area \
chandra-ao3_letg-hrc-s_letghi.area \
chandra-ao3_letg-hrc-s_order0.area \
             einstein_hri_.area    einstein_ipc_.area    einstein_mpc_.area \
             euve_ds_alc.area  euve_ds_lexb.area  euve_lw_.area  euve_mw_.area \
             euve_scanner_altic.area  euve_scanner_lexb.area  euve_sw_.area \
             exosat_gspc_.area     exosat_le_alp.area    exosat_le_brn.area \
             exosat_le_lx3.area    exosat_le_lx4.area    exosat_le_open.area \
             exosat_me_.area \
             ginga_lac_both.area          ginga_lac_top.area \
             heao1_a4_.area               heao1_a4_.special \
             rosat_hri_.area \
             rosat_pspc_brn.area   rosat_pspc_open.area  rosat_pspc_r1.area \
             rosat_pspc_r1r2.area  rosat_pspc_r2.area    rosat_pspc_r4.area \
             rosat_pspc_r4r5.area  rosat_pspc_r4tor7.area  rosat_pspc_r5.area \
             rosat_pspc_r6.area    rosat_pspc_r6r7.area  rosat_pspc_r7.area \
             rosat_wfc_p1.area            rosat_wfc_p2.area \
             rosat_wfc_s1.area            rosat_wfc_s2.area \
             sax_hpgspc_.area             sax_hpgspc_.special \
             sax_lecs_.area               sax_lecs_.special \
             sax_mecs_.area               sax_mecs_.special \
             sax_pds_.area         sax_pds_.special      sax_wfc_.area \
suzaku_hxd_gso.special  suzaku_hxd_gso.area  suzaku_xis_fi.area  suzaku_xrs_open.area \
	     suzaku_hxd_pin.area     suzaku_xrs_be.area      suzaku_xrs_open.special \
	     suzaku_xis_bi.area      suzaku_xis_bi.special suzaku_xrs_be.special \
	     suzaku_hxd_pin.special  suzaku_xis_fi.special \
	     swift_bat_single.area   swift_uvot_uvw2.area    swift_xrt_pd.area \
	     swift_uvot_b.area       swift_uvot_v.area       swift_xrt_pd.special \
	     swift_uvot_u.area       swift_uvot_vgrism.area  swift_xrt_wt.area \
	     swift_uvot_ugrism.area  swift_uvot_white.area   swift_xrt_wt.special \
	     swift_uvot_uvm2.area    swift_xrt_pc.area \
	     swift_uvot_uvw1.area    swift_xrt_pc.special \
	     swift_bat_single.special \
             xmm_mos_thick.area           xmm_mos_thick.special \
             xmm_mos_thin.area            xmm_mos_thin.special \
             xmm_pn_thick.area            xmm_pn_thick.special \
             xmm_pn_thin.area             xmm_pn_thin.special \
		xmm_mos_medium.area xmm_mos_medium.special \
		xmm_pn_medium.area xmm_pn_medium.special \
             xmm_rgs1_o1.area   xmm_rgs1_o2.area \
             xmm_rgs2_o1.area   xmm_rgs2_o2.area \
             xte_asm_.area                xte_asm_.special \
             xte_hexte_default.area       xte_hexte_default.special \
             xte_hexte_lld10.area         xte_hexte_lld10.special \
             xte_hexte_lld15.area         xte_hexte_lld15.special \
             xte_hexte_lld20.area         xte_hexte_lld20.special \
             xte_hexte_lld25.area         xte_hexte_lld25.special \
             xte_hexte_lld30.area         xte_hexte_lld30.special \
             xte_pca_.area                xte_pca_.special



include $(MK_TOP)/infra/Makefile.all
