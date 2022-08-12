#! /bin/tcsh -f
latex target
latex target2
latex too
latex proposal1
latex proposal2
latex proposal3
latex final_comments
latex alternate_target_group
latex rollreq
latex timereq
latex phasereq
latex conflicts
latex allotment
latex observatory
latex columns
latex views
latex sections
latex section_columns
latex table_columns
latex sorts
latex passwords

dvips -E -o target.eps target
dvips -E -o target2.eps target2
dvips -E -o too.eps too
dvips -E -o proposal1.eps proposal1
dvips -E -o proposal2.eps proposal2
dvips -E -o proposal3.eps proposal3
dvips -E -o final_comments.eps final_comments
dvips -E -o alternate_target_group.eps alternate_target_group
dvips -E -o rollreq.eps rollreq
dvips -E -o timereq.eps timereq
dvips -E -o phasereq.eps phasereq
dvips -E -o conflicts.eps conflicts
dvips -E -o allotment.eps allotment
dvips -E -o observatory.eps observatory
dvips -E -o columns.eps columns
dvips -E -o views.eps views
dvips -E -o sections.eps sections
dvips -E -o section_columns.eps section_columns
dvips -E -o table_columns.eps table_columns
dvips -E -o sorts.eps sorts
dvips -E -o passwords.eps passwords

