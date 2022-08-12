-- select p.panel_id, p.prop_id, p.type, big_proj, tc_prop, total_req_time, num_targ_req, p.num_obs_req, p.tax_req, tc_e_req, tc_a_req, tc_d_req, vf_req, f_req, s_req, vs_req, targ_id, alt_id, too_prob_req, req_time, t.num_obs_req, num_pnt_req, t.tax_req, tc_req, tc_grade_req, time_crit, monitor, raster_scan from proposal p, target t where p.prop_id = t.prop_id and p.panel_id = t.panel_id order by p.panel_id, p.prop_id, alt_id, targ_id;

 select p.panel_id, p.prop_id, p.type, big_proj, tc_prop, total_req_time, num_targ_req, p.num_obs_req, p.tax_req, rc_score_req, vf_req, f_req, s_req, vs_req, targ_id, alt_id, too_prob_req, req_time, t.num_obs_req, num_pnt_req, t.tax_req, t.rc_score,  time_crit, monitor, raster_scan from proposal p, target t where p.prop_id = t.prop_id and p.panel_id = t.panel_id order by p.panel_id, p.prop_id, alt_id, targ_id;

-- select panel_id, prop_id, targ_id, response_time as too, app_time as tc_uninter, uninterrupt as unint, tc_coor, tc_roll, tc_time, tc_phase, tc_monitor, tc_group_app, tc_too, tc_grade_app from target where time_crit = 'Y' order by prop_id, targ_id;

