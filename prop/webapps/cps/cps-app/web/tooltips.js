function buildTooltips(f) {
tooltips = new Array();
// Cover 
tooltips["multi_cycle"]="If yes then specify observing times for future cycles on the target page";
tooltips["linked_proposal"]="If Yes and proposal number is known, please enter the proposal number";
tooltips["linked_proposal_Y"]="If Yes and proposal number is known, please enter the proposal number";
tooltips["proposal_title"]="Maximum of 120 characters, required.";

// PI 
tooltips["first"]="First Name, maximum 20 chars, required.";
tooltips["last"]="Last Name, maximum 25 chars, required.";
tooltips["middle"]="Midde Name/Initial, maximum 20 chars, optional.";
tooltips["orcid"]="####-####-####-####";
tooltips["email"]="Maximum of 50 characters, required.";
tooltips["telephone"]="Maximum of 30 chars, required";
//CoI
tooltips["coi_first"]="First Name, maximum 20 chars, required.";
tooltips["coi_last"]="Last Name, maximum 25 chars, required.";
tooltips["coi_email"]="Maximum of 50 characters, required.";
tooltips["coi_phone"]="Only complete if the first Co-I is responsible for observing.";
tooltips["coi_institute"]="Institution list is filtered by the selected country. If an institution is not available, type it into the field.";
//Joint
tooltips["joint_type"]="Only use this field if this proposal has ALREADY BEEN ALLOCATED Chandra time after review by HST, NRAO, or XMM review panels.";
tooltips["hst_time"]="Valid integer values 1-250";
tooltips["xmm_time"]="Valid values .001-1000";
tooltips["swift_time"]="Valid values .001-500";
tooltips["nustar_time"]="Valid values .001-1000";
tooltips["noao_time"]="Valid values .1 - 99";
tooltips["nrao_time"]="Valid values .1-336";
//Target Manage
tooltips["clonetgt"]="If you want empty target forms, leave this as 'No'.  If you want the new targets to use the same parameters as one you've already created, indicate which target to clone.";
tooltips["tgtadd_type"]="Please specify the number of targets to add or specify the file containing the target parameters.";
tooltips["tgtadd_type_Specify"]="Please specify the number of targets to add, maximum of 50 targets at a time.";
tooltips["tgtadd_type_UPLOAD"]="Upload file should be text containing the fields Target Name, RA, Dec, Observing Time, Count Rate. If you select 'Clone Target' above, all other values will be copied from the original target. Otherwise, they will default to ACIS-S/NONE and other fields will be blank.";
//Supporting files
tooltips["upload_type"] = "Please choose the type of file to upload";
tooltips["upload_type_SJ"] = "Maximum size 10Mbytes.";
tooltips["upload_type_PC"] = "Maximum size 1Mbyte.";
tooltips["upload_type_TE"] = "Maximum size 4Mbytes.";
//Summary
tooltips["ready"] = "Checking this box indicates the proposal is complete. If necessary, proposals can be 'uncompleted' from the Home page and revised until the deadline.";

//Target Pointing
tooltips["vmagnitude"] = "Valid range -15.0 - 20.0";
tooltips["y_det_offset"] = "Valid values based on detector.  Overall range is -50.0 - 50.0";
tooltips["z_det_offset"] = "Valid values based on detector.  Overall range is -50.0 - 50.0";
tooltips["sim_trans_offset"] = "Valid values based on detector.  Overall range is -190.5 - 126.621";
//Target Time
tooltips["addmon"]="To add a row, click 'Add Observation' button. To edit existing row, double click in the field you want to modify.";
tooltips["clearmon"]="Removes all rows in the above table that have the 'Remove?' checkbox selected. Please remember to SAVE.";
tooltips["obs_time"]="Observing time must be > 1ks";
tooltips["obs_time_1"]="Observing time must be > 1ks";
tooltips["obs_time_2"]="Observing time must be > 1ks";
tooltips["trigger_target"]="Default is Yes. Use 'No' if this target is used to define a different instrument configuration.";
tooltips["trigger_target_N"]="No: This target is defining a different instrument configuration. Observing time must be 0, TOO details not available";
tooltips["trigger_target_Y"]="Yes: This is the trigger target for the TOO";
tooltips["split_interval"]="Total Observing Time <= Split Interval  < 2 years. 1 day = 86.4 ks";
//Target ACIS Optional
tooltips["frame_time"]="Valid range 0-10 in 1 second increments";
tooltips["subarray_start"]="Valid integer range 1-924";
tooltips["subarray_rows"]="Valid integer range 100-1024";
tooltips["secondary_exp_count"]="Valid range 0-15";
tooltips["primary_exposure_time"]="Valid range 0-10.0";
tooltips["eventfilter_lower"]="Valid range:\n ACIS-I:0.36-15.0\n ACIS-S:0.24-15.0";
tooltips["eventfilter_range"]="Valid range 0.1 - 15.0";
tooltips["subarray"]="Please ensure that your target falls within the chosen subarray";
tooltips["subarray_1/4"]="ACIS-I: Start Row=769, 256 rows\nACIS-S: Start Row=385, 256 rows";
tooltips["subarray_1/2"]="Start Row based on detector, 512 rows";
tooltips["subarray_1/8"]="Start Row based on detector, 128 rows";
tooltips["subarray_CUSTOM"]="Please ensure that your target falls within the spcified subarray";
tooltips["subarray_NONE"]="No subarray";
//Target Constraints
tooltips["winconstr"]="Check the box below the type of constraint you want to view/edit/add";
tooltips["rollconstr"]="Check the box below the type of constraint you want to view/edit/add";
tooltips["phconstr"]="Check the box below the type of constraint you want to view/edit/add";
tooltips["grpconstr"]="Check the box below the type of constraint you want to view/edit/add";
tooltips["coconstr"]="Check the box below the type of constraint you want to view/edit/add";
tooltips["addwin"]="To add a row, click 'Add Window Constraint' button. To edit existing row, double click in the field you want to modify.";
tooltips["clearwin"]="Remove all rows in the above table that have the 'Remove?' checkbox selected.";
tooltips["addroll"]="To add a row, click 'Add Roll Constraint' button. To edit existing row, double click in the field you want to modify.";
tooltips["clearroll"]="Remove all rows in the above table that have the 'Remove?' checkbox selected.";
tooltips["phase_epoch"]="Epoch must be within 5 years of the current date";
tooltips["phase_start"]="Values must be between 0 and 1";
tooltips["phase_start_margin"]="Values must be between 0 and .5";
tooltips["phase_end"]="Values must be between 0 and 1";
tooltips["phase_end_margin"]="Values must be between 0 and .5";
tooltips["group_interval"]="Valid range 0-364 days";
tooltips["coord_interval"]="Valid range 0-364 days";
tooltips["target_remarks"]="Target Remarks. Maximum 600 characters.";
// TOO Details
tooltips["start"]="Valid range .1-180";
tooltips["stop"]="Valid range is CXC Start - 365 days";
tooltips["probability"]="Valid values .1 - 1.0";



// Radio boxes
tooltipsRadio = new Array();
tooltipsRadio["multi_cycle"]= new Array("mprop");
tooltipsRadio["linked_proposal"]= new Array("lprop","Y");
tooltipsRadio["tgtadd_type"]=new Array("tgtaddlbl","Specify","UPLOAD");
tooltipsRadio["upload_type"]=new Array("uplbl","SJ","PC","TE");
tooltipsRadio["trigger_target"]=new Array("triggerlbl","Y","N");
tooltipsRadio["subarray"]= new Array("sublbl","1/4","1/2","1/8","NONE","CUSTOM");

  f.forEachItem(function(name){
     var eleType= f.getItemType(name);
     var tt = tooltips[name];
     if (tt) {
       if (eleType == "radio") {
         // the first item should be the label used for the radio boxes
         var lblarr = tooltipsRadio[name];
         if (lblarr.length > 0) {
           var lbl = lblarr[0];
           if (lbl) {
             f.setTooltip(lbl,tt);
             f.setUserData(lbl,tt);
           }
           for (var ll=1;ll<lblarr.length;ll++) {
             lbl = lblarr[ll];
             ttlbl = name + "_" + lblarr[ll];
             f.setTooltip(name,lbl,tooltips[ttlbl]);
             f.setUserData(name,lbl,"tooltip",tooltips[ttlbl]);
           }
         }
       } else {
         f.setTooltip(name,tt);
         f.setUserData(name,"tooltip",tt);
       }
     }
  });
}

