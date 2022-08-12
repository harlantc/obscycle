select p.prop_id as "Proposal" , rpad(last_name, 27) as "PI",
big_proj as "Type", 
total_app_time as "AppTime", 
title as "Title"
from proposal p 
where big_proj like '%P'
and prop_status = 'Y' 
order by p.prop_id;

