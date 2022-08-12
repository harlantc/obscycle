-- Create a tsv file containing chair /deputy/punditchair/punditdeputy, email
-- Used by pas_google_reports to add chairs to newly generated Doc.
-- Only works for current cycle.
-- Run like:
-- sqsh -U jcohen -S sqldev -i panel_prop_email_chairs.sql -L style=bcp -L  bcp_rowsep=' ' -L bcp_colsep='\t' -o panel_prop_email_chairs.tsv

use proposal
go

select email, panel_name
from proposal..panel pan, axafusers..person_short ps, proposal..context, proposal..panel_member pm
where pan.ao = context.current_ao_id
    and pm.pers_id = ps.pers_id
    and pan.panel_id = pm.panel_id
    and (pm.member_type like '%Chair%' or
    pm.member_type like '%Deputy%')
go