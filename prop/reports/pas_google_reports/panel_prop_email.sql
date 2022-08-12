-- Create a tsv file containing panel,proposal,prim_email,sec_email.
-- Used by pas_google_reports to generate new Docs or update comments en masse.
-- Only works for current cycle.
-- Run like:
-- sqsh -U jcohen -S sqldev -i panel_prop_email.sql -L style=bcp -L  bcp_rowsep=' ' -L bcp_colsep='\t' -o panel_prop_email.tsv

create table #dtmp
(
    proposal_number varchar(20),
    panel_name varchar(20),
    prim_email varchar(50),
    sec_email varchar(50)
)
go

use proposal
go

select proposal_number, panel_name,
    p.email "pri_email",
    s.email "sec_email"
from proposal, panel_proposal pp1 , panel p1, axafusers..person_short a1,
    proposal_review, axafusers..person_short p, axafusers..person_short s, context
where proposal_number like "%" and
    proposal.ao_str = current_ao_str and
    proposal.piid = a1.pers_id and
    proposal.proposal_id = pp1.proposal_id and
    pp1.panel_id = p1.panel_id and
    p1.panel_id = proposal_review.panel_id and
    proposal.proposal_id = proposal_review.proposal_id and
    proposal_review.primary_reviewer
*= p.pers_id and
    proposal_review.secondary_reviewer *= s.pers_id
    order by proposal.proposal_number
go