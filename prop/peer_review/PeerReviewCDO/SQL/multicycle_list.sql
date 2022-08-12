select p.prop_id as "Proposal" , rpad(last_name, 27) as "PI",
targ_num as "Tgt", 
rpad(targ_name, 21) as "Target", app_time as "AppTime", 
app_time_1 as "Cycle+1", app_time_2 as "Cycle+2" 
from proposal p, target t where p.prop_id = t.prop_id 
and p.panel_id = t.panel_id 
and targ_status = 'Y' and prop_status = 'Y' 
and t.mcop='Y'
order by t.prop_id,t.targ_num;

