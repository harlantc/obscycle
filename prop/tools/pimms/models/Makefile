#   ******************************************************************
#      Copyright (c) 1999   Smithsonian  Astrophysical  Observatory
#   The contents of this file are protected under the copyright notice
#   contained in the file  COPYRIGHT_SAO located in the root directory
#   of this distribution.
#   ******************************************************************

# Makefile for models subdirectory of pimms
#

MK_TOP = ../../../../..

include $(MK_TOP)/infra/Makefile.prop


# (The rs.idx file is needed (only) for the standard PIMMS R-S models (not for 
#  the "extra" models).)
PIMMS_MODELS =   rs.idx  mekal.idx apec.idx \
mekal02_600.mdl  mekal04_605.mdl  mekal06_610.mdl  mekal08_615.mdl  mekal10_620.mdl \
mekal02_605.mdl  mekal04_610.mdl  mekal06_615.mdl  mekal08_620.mdl  mekal10_625.mdl \
mekal02_610.mdl  mekal04_615.mdl  mekal06_620.mdl  mekal08_625.mdl  mekal10_630.mdl \
mekal02_615.mdl  mekal04_620.mdl  mekal06_625.mdl  mekal08_630.mdl  mekal10_635.mdl \
mekal02_620.mdl  mekal04_625.mdl  mekal06_630.mdl  mekal08_635.mdl  mekal10_640.mdl \
mekal02_625.mdl  mekal04_630.mdl  mekal06_635.mdl  mekal08_640.mdl  mekal10_645.mdl \
mekal02_630.mdl  mekal04_635.mdl  mekal06_640.mdl  mekal08_645.mdl  mekal10_650.mdl \
mekal02_635.mdl  mekal04_640.mdl  mekal06_645.mdl  mekal08_650.mdl  mekal10_655.mdl \
mekal02_640.mdl  mekal04_645.mdl  mekal06_650.mdl  mekal08_655.mdl  mekal10_660.mdl \
mekal02_645.mdl  mekal04_650.mdl  mekal06_655.mdl  mekal08_660.mdl  mekal10_665.mdl \
mekal02_650.mdl  mekal04_655.mdl  mekal06_660.mdl  mekal08_665.mdl  mekal10_670.mdl \
mekal02_655.mdl  mekal04_660.mdl  mekal06_665.mdl  mekal08_670.mdl  mekal10_675.mdl \
mekal02_660.mdl  mekal04_665.mdl  mekal06_670.mdl  mekal08_675.mdl  mekal10_680.mdl \
mekal02_665.mdl  mekal04_670.mdl  mekal06_675.mdl  mekal08_680.mdl  mekal10_685.mdl \
mekal02_670.mdl  mekal04_675.mdl  mekal06_680.mdl  mekal08_685.mdl  mekal10_690.mdl \
mekal02_675.mdl  mekal04_680.mdl  mekal06_685.mdl  mekal08_690.mdl  mekal10_695.mdl \
mekal02_680.mdl  mekal04_685.mdl  mekal06_690.mdl  mekal08_695.mdl  mekal10_700.mdl \
mekal02_685.mdl  mekal04_690.mdl  mekal06_695.mdl  mekal08_700.mdl  mekal10_705.mdl \
mekal02_690.mdl  mekal04_695.mdl  mekal06_700.mdl  mekal08_705.mdl  mekal10_710.mdl \
mekal02_695.mdl  mekal04_700.mdl  mekal06_705.mdl  mekal08_710.mdl  mekal10_715.mdl \
mekal02_700.mdl  mekal04_705.mdl  mekal06_710.mdl  mekal08_715.mdl  mekal10_720.mdl \
mekal02_705.mdl  mekal04_710.mdl  mekal06_715.mdl  mekal08_720.mdl  mekal10_725.mdl \
mekal02_710.mdl  mekal04_715.mdl  mekal06_720.mdl  mekal08_725.mdl  mekal10_730.mdl \
mekal02_715.mdl  mekal04_720.mdl  mekal06_725.mdl  mekal08_730.mdl  mekal10_735.mdl \
mekal02_720.mdl  mekal04_725.mdl  mekal06_730.mdl  mekal08_735.mdl  mekal10_740.mdl \
mekal02_725.mdl  mekal04_730.mdl  mekal06_735.mdl  mekal08_740.mdl  mekal10_745.mdl \
mekal02_730.mdl  mekal04_735.mdl  mekal06_740.mdl  mekal08_745.mdl  mekal10_750.mdl \
mekal02_735.mdl  mekal04_740.mdl  mekal06_745.mdl  mekal08_750.mdl  mekal10_755.mdl \
mekal02_740.mdl  mekal04_745.mdl  mekal06_750.mdl  mekal08_755.mdl  mekal10_760.mdl \
mekal02_745.mdl  mekal04_750.mdl  mekal06_755.mdl  mekal08_760.mdl  mekal10_765.mdl \
mekal02_750.mdl  mekal04_755.mdl  mekal06_760.mdl  mekal08_765.mdl  mekal10_770.mdl \
mekal02_755.mdl  mekal04_760.mdl  mekal06_765.mdl  mekal08_770.mdl  mekal10_775.mdl \
mekal02_760.mdl  mekal04_765.mdl  mekal06_770.mdl  mekal08_775.mdl  mekal10_780.mdl \
mekal02_765.mdl  mekal04_770.mdl  mekal06_775.mdl  mekal08_780.mdl  mekal10_785.mdl \
mekal02_770.mdl  mekal04_775.mdl  mekal06_780.mdl  mekal08_785.mdl  mekal10_790.mdl \
mekal02_775.mdl  mekal04_780.mdl  mekal06_785.mdl  mekal08_790.mdl  mekal10_795.mdl \
mekal02_780.mdl  mekal04_785.mdl  mekal06_790.mdl  mekal08_795.mdl  mekal10_800.mdl \
mekal02_785.mdl  mekal04_790.mdl  mekal06_795.mdl  mekal08_800.mdl  mekal10_805.mdl \
mekal02_790.mdl  mekal04_795.mdl  mekal06_800.mdl  mekal08_805.mdl  mekal10_810.mdl \
mekal02_795.mdl  mekal04_800.mdl  mekal06_805.mdl  mekal08_810.mdl  mekal10_815.mdl \
mekal02_800.mdl  mekal04_805.mdl  mekal06_810.mdl  mekal08_815.mdl  mekal10_820.mdl \
mekal02_805.mdl  mekal04_810.mdl  mekal06_815.mdl  mekal08_820.mdl  mekal10_825.mdl \
mekal02_810.mdl  mekal04_815.mdl  mekal06_820.mdl  mekal08_825.mdl  mekal10_830.mdl \
mekal02_815.mdl  mekal04_820.mdl  mekal06_825.mdl  mekal08_830.mdl  mekal10_835.mdl \
mekal02_820.mdl  mekal04_825.mdl  mekal06_830.mdl  mekal08_835.mdl  mekal10_840.mdl \
mekal02_825.mdl  mekal04_830.mdl  mekal06_835.mdl  mekal08_840.mdl  mekal10_845.mdl \
mekal02_830.mdl  mekal04_835.mdl  mekal06_840.mdl  mekal08_845.mdl  mekal10_850.mdl \
mekal02_835.mdl  mekal04_840.mdl  mekal06_845.mdl  mekal08_850.mdl  mekal02_840.mdl \
mekal04_845.mdl  mekal06_850.mdl  mekal10_600.mdl  mekal02_845.mdl  mekal04_850.mdl \
mekal08_600.mdl  mekal10_605.mdl  mekal02_850.mdl  mekal06_600.mdl  mekal08_605.mdl \
mekal10_610.mdl  mekal04_600.mdl  mekal06_605.mdl  mekal08_610.mdl  mekal10_615.mdl \
apec02_560.mdl  apec02_810.mdl  apec04_765.mdl  apec06_720.mdl  apec08_675.mdl  apec10_630.mdl \
apec02_565.mdl  apec02_815.mdl  apec04_770.mdl  apec06_725.mdl  apec08_680.mdl  apec10_635.mdl \
apec02_570.mdl  apec02_820.mdl  apec04_775.mdl  apec06_730.mdl  apec08_685.mdl  apec10_640.mdl \
apec02_575.mdl  apec02_825.mdl  apec04_780.mdl  apec06_735.mdl  apec08_690.mdl  apec10_645.mdl \
apec02_580.mdl  apec02_830.mdl  apec04_785.mdl  apec06_740.mdl  apec08_695.mdl  apec10_650.mdl \
apec02_585.mdl  apec02_835.mdl  apec04_790.mdl  apec06_745.mdl  apec08_700.mdl  apec10_655.mdl \
apec02_590.mdl  apec02_840.mdl  apec04_795.mdl  apec06_750.mdl  apec08_705.mdl  apec10_660.mdl \
apec02_595.mdl  apec02_845.mdl  apec04_800.mdl  apec06_755.mdl  apec08_710.mdl  apec10_665.mdl \
apec02_600.mdl  apec02_850.mdl  apec04_805.mdl  apec06_760.mdl  apec08_715.mdl  apec10_670.mdl \
apec02_605.mdl  apec04_560.mdl  apec04_810.mdl  apec06_765.mdl  apec08_720.mdl  apec10_675.mdl \
apec02_610.mdl  apec04_565.mdl  apec04_815.mdl  apec06_770.mdl  apec08_725.mdl  apec10_680.mdl \
apec02_615.mdl  apec04_570.mdl  apec04_820.mdl  apec06_775.mdl  apec08_730.mdl  apec10_685.mdl \
apec02_620.mdl  apec04_575.mdl  apec04_825.mdl  apec06_780.mdl  apec08_735.mdl  apec10_690.mdl \
apec02_625.mdl  apec04_580.mdl  apec04_830.mdl  apec06_785.mdl  apec08_740.mdl  apec10_695.mdl \
apec02_630.mdl  apec04_585.mdl  apec04_835.mdl  apec06_790.mdl  apec08_745.mdl  apec10_700.mdl \
apec02_635.mdl  apec04_590.mdl  apec04_840.mdl  apec06_795.mdl  apec08_750.mdl  apec10_705.mdl \
apec02_640.mdl  apec04_595.mdl  apec04_845.mdl  apec06_800.mdl  apec08_755.mdl  apec10_710.mdl \
apec02_645.mdl  apec04_600.mdl  apec04_850.mdl  apec06_805.mdl  apec08_760.mdl  apec10_715.mdl \
apec02_650.mdl  apec04_605.mdl  apec06_560.mdl  apec06_810.mdl  apec08_765.mdl  apec10_720.mdl \
apec02_655.mdl  apec04_610.mdl  apec06_565.mdl  apec06_815.mdl  apec08_770.mdl  apec10_725.mdl \
apec02_660.mdl  apec04_615.mdl  apec06_570.mdl  apec06_820.mdl  apec08_775.mdl  apec10_730.mdl \
apec02_665.mdl  apec04_620.mdl  apec06_575.mdl  apec06_825.mdl  apec08_780.mdl  apec10_735.mdl \
apec02_670.mdl  apec04_625.mdl  apec06_580.mdl  apec06_830.mdl  apec08_785.mdl  apec10_740.mdl \
apec02_675.mdl  apec04_630.mdl  apec06_585.mdl  apec06_835.mdl  apec08_790.mdl  apec10_745.mdl \
apec02_680.mdl  apec04_635.mdl  apec06_590.mdl  apec06_840.mdl  apec08_795.mdl  apec10_750.mdl \
apec02_685.mdl  apec04_640.mdl  apec06_595.mdl  apec06_845.mdl  apec08_800.mdl  apec10_755.mdl \
apec02_690.mdl  apec04_645.mdl  apec06_600.mdl  apec06_850.mdl  apec08_805.mdl  apec10_760.mdl \
apec02_695.mdl  apec04_650.mdl  apec06_605.mdl  apec08_560.mdl  apec08_810.mdl  apec10_765.mdl \
apec02_700.mdl  apec04_655.mdl  apec06_610.mdl  apec08_565.mdl  apec08_815.mdl  apec10_770.mdl \
apec02_705.mdl  apec04_660.mdl  apec06_615.mdl  apec08_570.mdl  apec08_820.mdl  apec10_775.mdl \
apec02_710.mdl  apec04_665.mdl  apec06_620.mdl  apec08_575.mdl  apec08_825.mdl  apec10_780.mdl \
apec02_715.mdl  apec04_670.mdl  apec06_625.mdl  apec08_580.mdl  apec08_830.mdl  apec10_785.mdl \
apec02_720.mdl  apec04_675.mdl  apec06_630.mdl  apec08_585.mdl  apec08_835.mdl  apec10_790.mdl \
apec02_725.mdl  apec04_680.mdl  apec06_635.mdl  apec08_590.mdl  apec08_840.mdl  apec10_795.mdl \
apec02_730.mdl  apec04_685.mdl  apec06_640.mdl  apec08_595.mdl  apec08_845.mdl  apec10_800.mdl \
apec02_735.mdl  apec04_690.mdl  apec06_645.mdl  apec08_600.mdl  apec08_850.mdl  apec10_805.mdl \
apec02_740.mdl  apec04_695.mdl  apec06_650.mdl  apec08_605.mdl  apec10_560.mdl  apec10_810.mdl \
apec02_745.mdl  apec04_700.mdl  apec06_655.mdl  apec08_610.mdl  apec10_565.mdl  apec10_815.mdl \
apec02_750.mdl  apec04_705.mdl  apec06_660.mdl  apec08_615.mdl  apec10_570.mdl  apec10_820.mdl \
apec02_755.mdl  apec04_710.mdl  apec06_665.mdl  apec08_620.mdl  apec10_575.mdl  apec10_825.mdl \
apec02_760.mdl  apec04_715.mdl  apec06_670.mdl  apec08_625.mdl  apec10_580.mdl  apec10_830.mdl \
apec02_765.mdl  apec04_720.mdl  apec06_675.mdl  apec08_630.mdl  apec10_585.mdl  apec10_835.mdl \
apec02_770.mdl  apec04_725.mdl  apec06_680.mdl  apec08_635.mdl  apec10_590.mdl  apec10_840.mdl \
apec02_775.mdl  apec04_730.mdl  apec06_685.mdl  apec08_640.mdl  apec10_595.mdl  apec10_845.mdl \
apec02_780.mdl  apec04_735.mdl  apec06_690.mdl  apec08_645.mdl  apec10_600.mdl  apec10_850.mdl \
apec02_785.mdl  apec04_740.mdl  apec06_695.mdl  apec08_650.mdl  apec10_605.mdl \
apec02_790.mdl  apec04_745.mdl  apec06_700.mdl  apec08_655.mdl  apec10_610.mdl \
apec02_795.mdl  apec04_750.mdl  apec06_705.mdl  apec08_660.mdl  apec10_615.mdl \
apec02_800.mdl  apec04_755.mdl  apec06_710.mdl  apec08_665.mdl  apec10_620.mdl \
apec02_805.mdl  apec04_760.mdl  apec06_715.mdl  apec08_670.mdl  apec10_625.mdl \
               rs02_560.mdl  rs02_565.mdl  rs02_570.mdl  rs02_575.mdl \
               rs02_580.mdl  rs02_585.mdl  rs02_590.mdl  rs02_595.mdl \
               rs02_600.mdl  rs02_605.mdl  rs02_610.mdl  rs02_615.mdl \
               rs02_620.mdl  rs02_625.mdl  rs02_630.mdl  rs02_635.mdl \
               rs02_640.mdl  rs02_645.mdl  rs02_650.mdl  rs02_655.mdl \
               rs02_660.mdl  rs02_665.mdl  rs02_670.mdl  rs02_675.mdl \
               rs02_680.mdl  rs02_685.mdl  rs02_690.mdl  rs02_695.mdl \
               rs02_700.mdl  rs02_705.mdl  rs02_710.mdl  rs02_715.mdl \
               rs02_720.mdl  rs02_725.mdl  rs02_730.mdl  rs02_735.mdl \
               rs02_740.mdl  rs02_745.mdl  rs02_750.mdl  rs02_755.mdl \
               rs02_760.mdl  rs02_765.mdl  rs02_770.mdl  rs02_775.mdl \
               rs02_780.mdl  rs02_785.mdl  rs02_790.mdl  rs02_795.mdl \
               rs02_800.mdl  rs02_805.mdl  rs02_810.mdl  rs02_815.mdl \
               rs02_820.mdl  rs02_825.mdl  rs02_830.mdl  rs02_835.mdl \
               rs02_840.mdl  rs02_845.mdl  rs02_850.mdl \
		rs04_560.mdl rs04_565.mdl rs04_570.mdl rs04_575.mdl \
		rs04_580.mdl rs04_585.mdl rs04_590.mdl rs04_595.mdl \
		rs04_600.mdl rs04_605.mdl rs04_610.mdl rs04_615.mdl \
		rs04_620.mdl rs04_625.mdl rs04_630.mdl rs04_635.mdl \
		rs04_640.mdl rs04_645.mdl rs04_650.mdl rs04_655.mdl \
		rs04_660.mdl rs04_665.mdl rs04_670.mdl rs04_675.mdl \
		rs04_680.mdl rs04_685.mdl rs04_690.mdl rs04_695.mdl \
		rs04_700.mdl rs04_705.mdl rs04_710.mdl rs04_715.mdl \
		rs04_720.mdl rs04_725.mdl rs04_730.mdl rs04_735.mdl \
		rs04_740.mdl rs04_745.mdl rs04_750.mdl rs04_755.mdl \
		rs04_760.mdl rs04_765.mdl rs04_770.mdl rs04_775.mdl \
		rs04_780.mdl rs04_785.mdl rs04_790.mdl rs04_795.mdl \
		rs04_800.mdl rs04_805.mdl rs04_810.mdl rs04_815.mdl \
		rs04_820.mdl rs04_825.mdl rs04_830.mdl rs04_835.mdl \
		rs04_840.mdl rs04_845.mdl rs04_850.mdl rs06_560.mdl \
		rs06_565.mdl rs06_570.mdl rs06_575.mdl rs06_580.mdl \
		rs06_585.mdl rs06_590.mdl rs06_595.mdl rs06_600.mdl \
		rs06_605.mdl rs06_610.mdl rs06_615.mdl rs06_620.mdl \
		rs06_625.mdl rs06_630.mdl rs06_635.mdl rs06_640.mdl \
		rs06_645.mdl rs06_650.mdl rs06_655.mdl rs06_660.mdl \
		rs06_665.mdl rs06_670.mdl rs06_675.mdl rs06_680.mdl \
		rs06_685.mdl rs06_690.mdl rs06_695.mdl rs06_700.mdl \
		rs06_705.mdl rs06_710.mdl rs06_715.mdl rs06_720.mdl \
		rs06_725.mdl rs06_730.mdl rs06_735.mdl rs06_740.mdl \
		rs06_745.mdl rs06_750.mdl rs06_755.mdl rs06_760.mdl \
		rs06_765.mdl rs06_770.mdl rs06_775.mdl rs06_780.mdl \
		rs06_785.mdl rs06_790.mdl rs06_795.mdl rs06_800.mdl \
		rs06_805.mdl rs06_810.mdl rs06_815.mdl rs06_820.mdl \
		rs06_825.mdl rs06_830.mdl rs06_835.mdl rs06_840.mdl \
		rs06_845.mdl rs06_850.mdl rs08_560.mdl rs08_565.mdl \
		rs08_570.mdl rs08_575.mdl rs08_580.mdl rs08_585.mdl \
		rs08_590.mdl rs08_595.mdl rs08_600.mdl rs08_605.mdl \
		rs08_610.mdl rs08_615.mdl rs08_620.mdl rs08_625.mdl \
		rs08_630.mdl rs08_635.mdl rs08_640.mdl rs08_645.mdl \
		rs08_650.mdl rs08_655.mdl rs08_660.mdl rs08_665.mdl \
		rs08_670.mdl rs08_675.mdl rs08_680.mdl rs08_685.mdl \
		rs08_690.mdl rs08_695.mdl rs08_700.mdl rs08_705.mdl \
		rs08_710.mdl rs08_715.mdl rs08_720.mdl rs08_725.mdl \
		rs08_730.mdl rs08_735.mdl rs08_740.mdl rs08_745.mdl \
		rs08_750.mdl rs08_755.mdl rs08_760.mdl rs08_765.mdl \
		rs08_770.mdl rs08_775.mdl rs08_780.mdl rs08_785.mdl \
		rs08_790.mdl rs08_795.mdl rs08_800.mdl rs08_805.mdl \
		rs08_810.mdl rs08_815.mdl rs08_820.mdl rs08_825.mdl \
		rs08_830.mdl rs08_835.mdl rs08_840.mdl rs08_845.mdl \
		rs08_850.mdl \
               rs10_560.mdl  rs10_565.mdl  rs10_570.mdl  rs10_575.mdl \
               rs10_580.mdl  rs10_585.mdl  rs10_590.mdl  rs10_595.mdl \
               rs10_600.mdl  rs10_605.mdl  rs10_610.mdl  rs10_615.mdl \
               rs10_620.mdl  rs10_625.mdl  rs10_630.mdl  rs10_635.mdl \
               rs10_640.mdl  rs10_645.mdl  rs10_650.mdl  rs10_655.mdl \
               rs10_660.mdl  rs10_665.mdl  rs10_670.mdl  rs10_675.mdl \
               rs10_680.mdl  rs10_685.mdl  rs10_690.mdl  rs10_695.mdl \
               rs10_700.mdl  rs10_705.mdl  rs10_710.mdl  rs10_715.mdl \
               rs10_720.mdl  rs10_725.mdl  rs10_730.mdl  rs10_735.mdl \
               rs10_740.mdl  rs10_745.mdl  rs10_750.mdl  rs10_755.mdl \
               rs10_760.mdl  rs10_765.mdl  rs10_770.mdl  rs10_775.mdl \
               rs10_780.mdl  rs10_785.mdl  rs10_790.mdl  rs10_795.mdl \
               rs10_800.mdl  rs10_805.mdl  rs10_810.mdl  rs10_815.mdl \
               rs10_820.mdl  rs10_825.mdl  rs10_830.mdl  rs10_835.mdl \
               rs10_840.mdl  rs10_845.mdl  rs10_850.mdl


include $(MK_TOP)/infra/Makefile.all
