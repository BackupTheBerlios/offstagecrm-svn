#!/bin/sh
# Usage: initdata name

psql $1 <<EOF

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('courseroles', 'courseroleid'), 3, true);

COPY courseroles (courseroleid, name, orderid) FROM stdin;
1	Student                       	1
2	Teacher                       	2
3	Pianist                       	3
\.

COPY dbversion (major, minor, rev) FROM stdin;
0	3	0
\.

COPY paymenttypeids (paymenttypeid, "table", name) FROM stdin;
2	cashpayments	Cash
3	checkpayments	Check
1	ccpayments	Credit Card (MC/Visa)
\.

COPY phoneids (groupid, name) FROM stdin;
8	other
9	second number
10	second fax
11	second work
12	cell
13	extension
14	fax
15	emergency
16	pager
17	school
18	work
19	home
\.
SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('relprimarytypes', 'relprimarytypeid'), 12, true);


COPY relprimarytypes (relprimarytypeid, name) FROM stdin;
1	
2	child
3	cousin
4	dss
5	parent
6	grandchild
7	grandparent
8	niece
9	sibling
10	spouse
11	au pair
12	ex-spouse
\.

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('tickettypes', 'tickettypeid'), 8, true);


COPY tickettypes (tickettypeid, tickettype) FROM stdin;
1	unknown
2	voucher
3	adult
4	company
5	free
6	senior
7	vip
8	usher
\.

COPY noteids (groupid, name) FROM stdin;
88	NOTES
\.

EOF
