select panel_id as pan, prop_id, targ_id, num_obs_app, num_pnt_app, app_time from target where targ_status = 'Y' and monitor = 'Y' order by panel_id, prop_id;
