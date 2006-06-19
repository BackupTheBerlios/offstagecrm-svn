
--
-- PostgreSQL database dump
--

SET client_encoding = 'UTF8';
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON SCHEMA public IS 'Standard public schema';


--
-- Name: plpgsql; Type: PROCEDURAL LANGUAGE; Schema: -; Owner: 
--

--CREATE PROCEDURAL LANGUAGE plpgsql;


SET search_path = public, pg_catalog;

--
-- Name: r_entities_idlist_name_ret; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE r_entities_idlist_name_ret AS (
	entityid integer,
	name character varying
);


--ALTER TYPE public.r_entities_idlist_name_ret OWNER TO $1;

--
-- Name: absences; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE absences (
    entityid integer NOT NULL,
    meetingid integer NOT NULL,
    dtparentreviewed timestamp without time zone,
    dtstaffreviewed timestamp without time zone,
    parentnotes text,
    staffnotes text,
    dtime timestamp without time zone,
    privatenotes text,
    dtparentnotified timestamp without time zone,
    parentemail character varying(50),
    "valid" boolean DEFAULT true NOT NULL
);


-- ALTER TABLE public.absences OWNER TO $1;

--
-- Name: TABLE absences; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE absences IS 'Record whenever an absence is noted';


--
-- Name: COLUMN absences.dtparentreviewed; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN absences.dtparentreviewed IS 'Date/Time parent first reviewed the absence';


--
-- Name: COLUMN absences.dtstaffreviewed; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN absences.dtstaffreviewed IS 'Date/Time staff first reviewed the absence';


--
-- Name: COLUMN absences.parentnotes; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN absences.parentnotes IS 'Notations entered by parent, visible to staff';


--
-- Name: COLUMN absences.staffnotes; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN absences.staffnotes IS 'Notations entered by staff, visible to parent';


--
-- Name: COLUMN absences.dtime; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN absences.dtime IS 'Date/Time absence was noted by system';


--
-- Name: COLUMN absences.privatenotes; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN absences.privatenotes IS 'Private notes viewable by staff only';


--
-- Name: COLUMN absences.dtparentnotified; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN absences.dtparentnotified IS 'Date/Time parent emailed about absence';


--
-- Name: COLUMN absences.parentemail; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN absences.parentemail IS 'Address to which parent notification was sent';


--
-- Name: COLUMN absences."valid"; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN absences."valid" IS 'Was this a real absence, or was there a glitch in the system?';


--
-- Name: accounts; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE accounts (
    entityid integer NOT NULL,
    username character varying(50) NOT NULL,
    "password" character varying(15) NOT NULL
);


-- ALTER TABLE public.accounts OWNER TO $1;

--
-- Name: TABLE accounts; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE accounts IS 'Userid and login for entities with login accounts';


--
-- Name: COLUMN accounts."password"; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN accounts."password" IS 'Not encrypted --- we might have to email it to parents';


--
-- Name: attendance; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE attendance (
    meetingid integer,
    entityid integer,
    dtime timestamp without time zone DEFAULT now()
);


-- ALTER TABLE public.attendance OWNER TO $1;

--
-- Name: TABLE attendance; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE attendance IS 'Who actually attended what courses';


--
-- Name: COLUMN attendance.dtime; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN attendance.dtime IS 'Time attendance was noted';


--
-- Name: paymentids; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE paymentids (
    paymentid serial NOT NULL,
    entityid integer,
    amount numeric(9,2),
    dtime timestamp without time zone DEFAULT now() NOT NULL,
    date date NOT NULL,
    remain numeric(9,2) NOT NULL
);


-- ALTER TABLE public.paymentids OWNER TO $1;

--
-- Name: COLUMN paymentids.entityid; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN paymentids.entityid IS 'Person doing the paying';


--
-- Name: COLUMN paymentids.dtime; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN paymentids.dtime IS 'Date/time payment was recorded in our system';


--
-- Name: COLUMN paymentids.date; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN paymentids.date IS 'Date payment received (as perceived by human taking payment)';


--
-- Name: COLUMN paymentids.remain; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN paymentids.remain IS 'Unallocated $$ remaining from payment';


--
-- Name: cashpayments; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE cashpayments (
)
INHERITS (paymentids);


-- ALTER TABLE public.cashpayments OWNER TO $1;

--
-- Name: ccardbalance; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE ccardbalance (
    personid integer,
    balance numeric(9,2),
    dtime timestamp without time zone
);


-- ALTER TABLE public.ccardbalance OWNER TO $1;

--
-- Name: COLUMN ccardbalance.personid; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN ccardbalance.personid IS 'Person for whom this is a class card';


--
-- Name: COLUMN ccardbalance.balance; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN ccardbalance.balance IS 'Amount in class card account.';


--
-- Name: COLUMN ccardbalance.dtime; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN ccardbalance.dtime IS 'Date/Time the balance is valid';


--
-- Name: ccardtrans; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE ccardtrans (
    entityid integer,
    dtime timestamp without time zone,
    meetingid integer,
    amount numeric(9,2)
);


-- ALTER TABLE public.ccardtrans OWNER TO $1;

--
-- Name: COLUMN ccardtrans.dtime; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN ccardtrans.dtime IS 'Date/Time of transaction';


--
-- Name: COLUMN ccardtrans.meetingid; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN ccardtrans.meetingid IS 'Meeting student is attending (if amount is negative)';


--
-- Name: COLUMN ccardtrans.amount; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN ccardtrans.amount IS 'Amount credited (positive) or debited (negative)';


--
-- Name: ccpayments; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE ccpayments (
    cctype character(1) NOT NULL,
    ccnumber character varying(20),
    invaliddate date,
    name character varying(50),
    dtprocessed timestamp with time zone,
    status character(1)
)
INHERITS (paymentids);


-- ALTER TABLE public.ccpayments OWNER TO $1;

--
-- Name: COLUMN ccpayments.dtime; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN ccpayments.dtime IS 'Time cc transaction posted to this DB';


--
-- Name: COLUMN ccpayments.date; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN ccpayments.date IS 'Date of CC transaction';


--
-- Name: COLUMN ccpayments.cctype; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN ccpayments.cctype IS '''v'' or ''m'' for Visa or MasterCard';


--
-- Name: COLUMN ccpayments.ccnumber; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN ccpayments.ccnumber IS 'Just last 4 digits of cc #, once payment has been processed';


--
-- Name: COLUMN ccpayments.invaliddate; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN ccpayments.invaliddate IS 'First date on which card is NOT valid.';


--
-- Name: COLUMN ccpayments.name; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN ccpayments.name IS 'Name as appears on card';


--
-- Name: COLUMN ccpayments.dtprocessed; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN ccpayments.dtprocessed IS 'Date/Time payment was put through to the CC company';


--
-- Name: COLUMN ccpayments.status; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN ccpayments.status IS '''a'' or ''r'' for accepted or rejected';


--
-- Name: checkpayments; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE checkpayments (
    name character varying(50),
    checknumber integer,
    phone character varying(30),
    status character(1)
)
INHERITS (paymentids);


-- ALTER TABLE public.checkpayments OWNER TO $1;

--
-- Name: COLUMN checkpayments.name; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN checkpayments.name IS 'Name as appears on check';


--
-- Name: COLUMN checkpayments.phone; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN checkpayments.phone IS 'Phone number on check';


--
-- Name: COLUMN checkpayments.status; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN checkpayments.status IS '''r'' if check bounced';


--
-- Name: coursedeps; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE coursedeps (
    basecourseid integer,
    reqcourseid integer
);


-- ALTER TABLE public.coursedeps OWNER TO $1;

--
-- Name: TABLE coursedeps; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE coursedeps IS 'Courses required if one is enrolling in other courses';


--
-- Name: COLUMN coursedeps.basecourseid; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN coursedeps.basecourseid IS 'The course one WANTS to take';


--
-- Name: COLUMN coursedeps.reqcourseid; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN coursedeps.reqcourseid IS 'The course one is REQUIRED to take';


--
-- Name: courseids; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE courseids (
    courseid serial NOT NULL,
    name character varying(50),
    termid integer,
    dayofweek integer,
    tstart time without time zone,
    tnext time without time zone,
    "limit" integer
);


-- ALTER TABLE public.courseids OWNER TO $1;

--
-- Name: COLUMN courseids.dayofweek; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN courseids.dayofweek IS 'Uses Java day-of-week convention';


--
-- Name: COLUMN courseids.tnext; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN courseids.tnext IS 'Normal end time of course';


--
-- Name: COLUMN courseids."limit"; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN courseids."limit" IS 'Max # of students to be enrolled (guideline)';


--
-- Name: courseroles; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE courseroles (
    courseroleid serial NOT NULL,
    name character(30),
    orderid integer
);


-- ALTER TABLE public.courseroles OWNER TO $1;

--
-- Name: TABLE courseroles; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE courseroles IS 'Types of enrollment in a course (student, teacher, etc)';


--
-- Name: coursesetids; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE coursesetids (
    coursesetid serial NOT NULL,
    programid integer,
    name character varying(50)
);


-- ALTER TABLE public.coursesetids OWNER TO $1;

--
-- Name: TABLE coursesetids; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE coursesetids IS 'Sets of courses --- used to give customers simple menus for enrollment.';


--
-- Name: COLUMN coursesetids.programid; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN coursesetids.programid IS 'Program to which this course set belongs (all courses in it must have matching programid)';


--
-- Name: coursesets; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE coursesets (
    coursesetid integer,
    courseid integer
);


-- ALTER TABLE public.coursesets OWNER TO $1;

SET default_with_oids = false;

--
-- Name: dbversion; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE dbversion (
    major integer DEFAULT 0 NOT NULL,
    minor integer DEFAULT 0 NOT NULL,
    rev integer DEFAULT 0 NOT NULL
);


-- ALTER TABLE public.dbversion OWNER TO $1;

--
-- Name: groupids; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE groupids (
    groupid serial NOT NULL,
    name character varying(100) NOT NULL
);


-- ALTER TABLE public.groupids OWNER TO $1;

--
-- Name: dtgroupids; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE dtgroupids (
)
INHERITS (groupids);


-- ALTER TABLE public.dtgroupids OWNER TO $1;

--
-- Name: donationids; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE donationids (
)
INHERITS (dtgroupids);


-- ALTER TABLE public.donationids OWNER TO $1;

--
-- Name: groups; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE groups (
    groupid integer NOT NULL,
    entityid integer NOT NULL
);


-- ALTER TABLE public.groups OWNER TO $1;

--
-- Name: dtgroups; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE dtgroups (
    groupid integer,
    entityid integer,
    date date NOT NULL
)
INHERITS (groups);


-- ALTER TABLE public.dtgroups OWNER TO $1;

--
-- Name: donations; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE donations (
    amount numeric(9,2)
)
INHERITS (dtgroups);


-- ALTER TABLE public.donations OWNER TO $1;

SET default_with_oids = true;

--
-- Name: enrollments; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE enrollments (
    courseid integer NOT NULL,
    entityid integer NOT NULL,
    courserole integer NOT NULL,
    dstart date,
    dend date,
    pplanid integer,
    dtapproved time without time zone,
    dtenrolled timestamp without time zone
);


-- ALTER TABLE public.enrollments OWNER TO $1;

--
-- Name: COLUMN enrollments.pplanid; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN enrollments.pplanid IS 'Payment plan set up to pay for this (and other) enrollment';


--
-- Name: COLUMN enrollments.dtapproved; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN enrollments.dtapproved IS 'Date/Time principlal approved enrollment';


--
-- Name: COLUMN enrollments.dtenrolled; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN enrollments.dtenrolled IS 'Date/Time student enrolled';


SET default_with_oids = false;

--
-- Name: entities; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE entities (
    entityid serial NOT NULL,
    oldid integer,
    primaryentityid integer,
    address1 character varying(100),
    address2 character varying(100),
    city character varying(50),
    state character varying(20),
    zip character varying(11),
    country character varying(50),
    recordsource character varying(25),
    sourcekey integer,
    lastupdated timestamp without time zone,
    relprimarytypeid integer,
    sendmail boolean DEFAULT true,
    obsolete boolean DEFAULT false,
    created timestamp without time zone DEFAULT now(),
    adultid integer,
    title character varying(60),
    occupation character varying(60),
    salutation character varying(30),
    firstname character varying(50),
    middlename character varying(50),
    lastname character varying(50),
    customaddressto character varying(100)
);


-- ALTER TABLE public.entities OWNER TO $1;

--
-- Name: eventids; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE eventids (
    "comment" character varying(100)
)
INHERITS (groupids);


-- ALTER TABLE public.eventids OWNER TO $1;

--
-- Name: events; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE events (
    "role" character varying(50)
)
INHERITS (groups);


-- ALTER TABLE public.events OWNER TO $1;

--
-- Name: interestids; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE interestids (
    "comment" character varying(200)
)
INHERITS (groupids);


-- ALTER TABLE public.interestids OWNER TO $1;

--
-- Name: interests; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE interests (
    byperson boolean,
    referredby character varying(50)
)
INHERITS (groups);


-- ALTER TABLE public.interests OWNER TO $1;

SET default_with_oids = true;

--
-- Name: invoiceids; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE invoiceids (
    invoiceid serial NOT NULL,
    "type" character(1) NOT NULL,
    amount numeric(9,2),
    dtime timestamp without time zone DEFAULT now() NOT NULL,
    entityid integer NOT NULL,
    remain numeric(9,2),
    ddue date
);


-- ALTER TABLE public.invoiceids OWNER TO $1;

--
-- Name: COLUMN invoiceids."type"; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN invoiceids."type" IS '''e'' = enrollment, ''p'' = pledge, ''t'' = ticket sale ''c''=class card, ''s'' = sub';


--
-- Name: COLUMN invoiceids.amount; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN invoiceids.amount IS 'Can be calculated by enrollments, pledges or ticket sales associated with this invoice.';


--
-- Name: COLUMN invoiceids.dtime; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN invoiceids.dtime IS 'Date/time invoice created';


--
-- Name: COLUMN invoiceids.entityid; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN invoiceids.entityid IS 'Person responsible for the invoice';


--
-- Name: COLUMN invoiceids.remain; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN invoiceids.remain IS 'Amount remaining to be paid on invoice';


--
-- Name: COLUMN invoiceids.ddue; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN invoiceids.ddue IS 'Due date by which invoice must be paid';


SET default_with_oids = false;

--
-- Name: mailingids; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE mailingids (
    created timestamp without time zone DEFAULT now(),
    equery text
)
INHERITS (groupids);


-- ALTER TABLE public.mailingids OWNER TO $1;

SET default_with_oids = true;

--
-- Name: mailings; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE mailings (
    ename character varying(100),
    addressto character varying(100),
    address1 character varying(100),
    address2 character varying(100),
    city character varying(50),
    state character varying(50),
    zip character varying(30),
    sendentityid integer,
    minoid integer,
    country character varying(50),
    line1 character varying(100),
    line2 character varying(100),
    line3 character varying(100),
    isgood boolean
)
INHERITS (groups);


-- ALTER TABLE public.mailings OWNER TO $1;

--
-- Name: meetings; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE meetings (
    meetingid serial NOT NULL,
    courseid integer NOT NULL,
    dtstart timestamp without time zone NOT NULL,
    dtnext timestamp without time zone NOT NULL
);


-- ALTER TABLE public.meetings OWNER TO $1;

--
-- Name: COLUMN meetings.courseid; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN meetings.courseid IS 'Course to which this meeting belongs';


SET default_with_oids = false;

--
-- Name: noteids; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE noteids (
)
INHERITS (dtgroupids);


-- ALTER TABLE public.noteids OWNER TO $1;

--
-- Name: notes; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE notes (
    note text
)
INHERITS (dtgroups);


-- ALTER TABLE public.notes OWNER TO $1;

--
-- Name: organizations; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE organizations (
    name character varying(100)
)
INHERITS (entities);


-- ALTER TABLE public.organizations OWNER TO $1;

SET default_with_oids = true;

--
-- Name: paymentallocs; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE paymentallocs (
    invoiceid integer NOT NULL,
    paymentid integer NOT NULL,
    amount numeric(9,2) NOT NULL
);


-- ALTER TABLE public.paymentallocs OWNER TO $1;

--
-- Name: paymenttypeids; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE paymenttypeids (
    paymenttypeid integer NOT NULL,
    "table" character varying(30) NOT NULL,
    name character varying(50)
);


-- ALTER TABLE public.paymenttypeids OWNER TO $1;

--
-- Name: COLUMN paymenttypeids."table"; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN paymenttypeids."table" IS 'Table in DB that holdes this type of payment';


SET default_with_oids = false;

--
-- Name: persons; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE persons (
    gender character(1),
    dob date,
    email character varying(100)
)
INHERITS (entities);


-- ALTER TABLE public.persons OWNER TO $1;

--
-- Name: phoneids; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE phoneids (
)
INHERITS (groupids);


-- ALTER TABLE public.phoneids OWNER TO $1;

--
-- Name: phones; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE phones (
    phone character varying(20)
)
INHERITS (groups);


-- ALTER TABLE public.phones OWNER TO $1;

SET default_with_oids = true;

--
-- Name: pplanids; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE pplanids (
    pplanid serial NOT NULL,
    entityid integer NOT NULL,
    paymenttypeid integer NOT NULL,
    cctype character(1),
    ccnumber character varying(25),
    invaliddate date,
    name character varying(50),
    dtime timestamp without time zone DEFAULT now(),
    dtapproved timestamp without time zone,
    paymentplantypeid integer,
    remain numeric(9,2),
    amount numeric(9,2),
    termid integer
);


-- ALTER TABLE public.pplanids OWNER TO $1;

--
-- Name: COLUMN pplanids.cctype; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN pplanids.cctype IS '''m'' or ''v'' for MasterCard/Visa';


--
-- Name: COLUMN pplanids.ccnumber; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN pplanids.ccnumber IS 'Full CC #';


--
-- Name: COLUMN pplanids.dtime; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN pplanids.dtime IS 'Date/Time payment method supplied by customer';


--
-- Name: COLUMN pplanids.dtapproved; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN pplanids.dtapproved IS 'Date/Time this payment method was approved';


--
-- Name: COLUMN pplanids.paymentplantypeid; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN pplanids.paymentplantypeid IS 'Spacing of payments --- in full @ beginning, in quarterly installments, etc.';


--
-- Name: COLUMN pplanids.remain; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN pplanids.remain IS 'Amount remaining on the payment plan --- or null if amount has not yet been determined.';


--
-- Name: COLUMN pplanids.amount; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN pplanids.amount IS 'Amount of $$ for which payment plan is being set up';


--
-- Name: COLUMN pplanids.termid; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN pplanids.termid IS 'Term for payment play --- determines schedule of payments';


--
-- Name: pplaninvoiceids; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE pplaninvoiceids (
    pplanid integer,
    invoiceid integer
);


-- ALTER TABLE public.pplaninvoiceids OWNER TO $1;

--
-- Name: TABLE pplaninvoiceids; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE pplaninvoiceids IS 'Invoices generated that are associated with a payment plan';


--
-- Name: pplantypeids; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE pplantypeids (
    pplantypeid integer NOT NULL,
    "type" character varying(30) NOT NULL,
    name character varying(50) NOT NULL
);


-- ALTER TABLE public.pplantypeids OWNER TO $1;

--
-- Name: programids; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE programids (
    programid serial NOT NULL,
    termid integer,
    name character varying(50),
    needselig boolean DEFAULT true NOT NULL,
    minage integer
);


-- ALTER TABLE public.programids OWNER TO $1;

--
-- Name: COLUMN programids.termid; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN programids.termid IS 'Term with which this program is associated --- or no term';


--
-- Name: COLUMN programids.needselig; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN programids.needselig IS 'Is an eligibility record required to registe for this program?';


--
-- Name: COLUMN programids.minage; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN programids.minage IS 'Minimum age (in years) for eligibility for this open program';


--
-- Name: regelig; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE regelig (
    programid integer NOT NULL,
    entityid integer NOT NULL,
    authdtime timestamp without time zone,
    expiredate date
);


-- ALTER TABLE public.regelig OWNER TO $1;

--
-- Name: TABLE regelig; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE regelig IS 'Permission to register for programs';


--
-- Name: COLUMN regelig.authdtime; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN regelig.authdtime IS 'Date/time eligibility was authorized';


--
-- Name: COLUMN regelig.expiredate; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN regelig.expiredate IS 'Date eligibility to register expires';


--
-- Name: registrations; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE registrations (
    programid integer,
    entityid integer,
    regdtime timestamp without time zone,
    expiredate date
);


-- ALTER TABLE public.registrations OWNER TO $1;

--
-- Name: COLUMN registrations.regdtime; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN registrations.regdtime IS 'Date/time of registration';


--
-- Name: COLUMN registrations.expiredate; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN registrations.expiredate IS 'Date this registration expires --- usually @ end of YDP term, or 1 yr. from open class registration.';


SET default_with_oids = false;

--
-- Name: relprimarytypes; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE relprimarytypes (
    relprimarytypeid serial NOT NULL,
    name character varying(30)
);


-- ALTER TABLE public.relprimarytypes OWNER TO $1;

--
-- Name: status; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE status (
)
INHERITS (groups);


-- ALTER TABLE public.status OWNER TO $1;

--
-- Name: statusids; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE statusids (
)
INHERITS (groupids);


-- ALTER TABLE public.statusids OWNER TO $1;

SET default_with_oids = true;

--
-- Name: subs; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE subs (
    meetingid integer NOT NULL,
    entityid integer NOT NULL,
    subtype character(1) NOT NULL,
    courserole integer,
    dtapproved timestamp without time zone,
    enterdtime timestamp without time zone DEFAULT now()
);


-- ALTER TABLE public.subs OWNER TO $1;

--
-- Name: COLUMN subs.subtype; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN subs.subtype IS '''+'' or ''-''';


--
-- Name: COLUMN subs.courserole; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN subs.courserole IS 'If ''+'', role this person will play at this course meeting';


--
-- Name: COLUMN subs.dtapproved; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN subs.dtapproved IS 'Date/time office staff reviewed & approved the time --- initially set to null if parent reports absence automatically via website.';


--
-- Name: COLUMN subs.enterdtime; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN subs.enterdtime IS 'Date/time change first entered into system';


--
-- Name: termids; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE termids (
    termid serial NOT NULL,
    termtypeid integer NOT NULL,
    name character varying(40),
    firstdate date NOT NULL,
    nextdate date NOT NULL,
    iscurrent boolean
);


-- ALTER TABLE public.termids OWNER TO $1;

--
-- Name: TABLE termids; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE termids IS 'Terms over which courses run';


--
-- Name: termtypes; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE termtypes (
    termtypeid serial NOT NULL,
    name character varying(40) NOT NULL,
    orderid integer NOT NULL
);


-- ALTER TABLE public.termtypes OWNER TO $1;

--
-- Name: TABLE termtypes; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE termtypes IS 'Enumerate type for kinds of terms';


--
-- Name: COLUMN termtypes.orderid; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN termtypes.orderid IS 'default ordering in dropdowns';


SET default_with_oids = false;

--
-- Name: ticketeventids; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE ticketeventids (
)
INHERITS (groupids);


-- ALTER TABLE public.ticketeventids OWNER TO $1;

--
-- Name: ticketeventsales; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE ticketeventsales (
    numberoftickets integer,
    payment numeric(9,2),
    tickettypeid integer
)
INHERITS (groups);


-- ALTER TABLE public.ticketeventsales OWNER TO $1;

--
-- Name: tickettypes; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tickettypes (
    tickettypeid serial NOT NULL,
    tickettype character varying(20)
);


-- ALTER TABLE public.tickettypes OWNER TO $1;

--
-- Name: dbversion_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY dbversion
    ADD CONSTRAINT dbversion_pkey PRIMARY KEY (major, minor, rev);


--
-- Name: dtgroups_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY dtgroups
    ADD CONSTRAINT dtgroups_pkey PRIMARY KEY (groupid, entityid, date);


--
-- Name: enrollments_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY enrollments
    ADD CONSTRAINT enrollments_pkey PRIMARY KEY (courseid, entityid);


--
-- Name: entities_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY entities
    ADD CONSTRAINT entities_pkey PRIMARY KEY (entityid);


--
-- Name: groupids_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY groupids
    ADD CONSTRAINT groupids_pkey PRIMARY KEY (groupid);


--
-- Name: groups_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY groups
    ADD CONSTRAINT groups_pkey PRIMARY KEY (groupid, entityid);


--
-- Name: noteids_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY noteids
    ADD CONSTRAINT noteids_pkey PRIMARY KEY (groupid);


--
-- Name: phoneids_name_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY phoneids
    ADD CONSTRAINT phoneids_name_key UNIQUE (name);


--
-- Name: relprimarytypes_name_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY relprimarytypes
    ADD CONSTRAINT relprimarytypes_name_key UNIQUE (name);


--
-- Name: relprimarytypes_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY relprimarytypes
    ADD CONSTRAINT relprimarytypes_pkey PRIMARY KEY (relprimarytypeid);


--
-- Name: subs_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY subs
    ADD CONSTRAINT subs_pkey PRIMARY KEY (meetingid, entityid);


--
-- Name: tickettypes_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY tickettypes
    ADD CONSTRAINT tickettypes_pkey PRIMARY KEY (tickettypeid);


--
-- Name: entities_relprimarytypeid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY entities
    ADD CONSTRAINT entities_relprimarytypeid_fkey FOREIGN KEY (relprimarytypeid) REFERENCES relprimarytypes(relprimarytypeid);


--
-- Name: groups_entityid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY groups
    ADD CONSTRAINT groups_entityid_fkey FOREIGN KEY (entityid) REFERENCES entities(entityid);


--
-- Name: groups_groupid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY groups
    ADD CONSTRAINT groups_groupid_fkey FOREIGN KEY (groupid) REFERENCES groupids(groupid);


--
-- Name: ticketeventsales_tickettypeid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ticketeventsales
    ADD CONSTRAINT ticketeventsales_tickettypeid_fkey FOREIGN KEY (tickettypeid) REFERENCES tickettypes(tickettypeid);


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

-- ================================= Functions


--
-- Name: dropsilent2(character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION dropsilent2(character varying) RETURNS integer
    AS $_$DECLARE
   table alias for $1;
   rnn varchar;
   sql varchar;
BEGIN

SELECT into rnn relname FROM pg_class WHERE relname = table;
if (rnn is not null) then
     sql := 'drop table ' || table;
     EXECUTE sql;
end if;

END
$_$
    LANGUAGE plpgsql;


-- ALTER FUNCTION public.dropsilent2(character varying) OWNER TO $1;

--
-- Name: entityname(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION entityname(integer) RETURNS character varying
    AS $_$DECLARE
	vrelname varchar;
	vname varchar;
BEGIN
	select into vrelname relname
	from entities e, pg_class c
	where e.tableoid = c.oid
	and e.entityid = $1;

	if vrelname = 'persons' then
		select into vname
		case when lastname is null then '' else lastname || ', ' end ||
		case when firstname is null then '' else firstname || ' ' end ||
		case when middlename is null then '' else middlename end
		from persons
		where entityid = $1;
	else
		select into vname name
		from organizations
		where entityid = $1;
	end if;
	return rtrim(vname);
END








$_$
    LANGUAGE plpgsql;


-- ALTER FUNCTION public.entityname(integer) OWNER TO $1;



--
-- Name: r_entities_idlist_name(character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION r_entities_idlist_name(character varying) RETURNS SETOF int2vector
    AS $_$DECLARE
        veqsql alias for $1;
        sql text;                      -- SQL we'll put together to insert records
        r int;
BEGIN

-- Create temporary table of IDs for this mailing list
perform dropsilent('_ids');
create temporary table _ids (entityid int);
sql := 'insert into _ids (entityid) ' || veqsql;
execute sql;

// -------------------
for r in
(select o.entityid, 'organizations' as relation, name as name
from organizations o, ids
where o.entityid = ids.entityid
  union
select p.entityid, 'persons' as relation,
(case when lastname is null then '' else lastname || ', ' end ||
case when firstname is null then '' else firstname || ' ' end ||
case when middlename is null then '' else middlename end) as name
from persons p, _ids
where p.entityid = ids.entityid)
order by relation, name

        return next r;

end loop;

// -------------------
drop table _ids;

return;

END
$_$
    LANGUAGE plpgsql;


-- ALTER FUNCTION public.r_entities_idlist_name(character varying) OWNER TO $1;

--
-- Name: r_entities_idlist_name2(character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION r_entities_idlist_name2(character varying) RETURNS SETOF r_entities_idlist_name_ret
    AS $_$DECLARE
        veqsql alias for $1;
        sql text;                      -- SQL we'll put together to insert records
        r r_entities_idlist_name_ret;
BEGIN

-- Create temporary table of IDs for this mailing list
perform dropsilent('_ids');
create temporary table _ids (entityid int);
sql := 'insert into _ids (entityid) ' || veqsql;
execute sql;

-- -------------------
for r in
(select o.entityid, 'organizations' as relation, name as name
from organizations o, _ids
where o.entityid = _ids.entityid
  union
select p.entityid, 'persons' as relation,
(case when lastname is null then '' else lastname || ', ' end ||
case when firstname is null then '' else firstname || ' ' end ||
case when middlename is null then '' else middlename end) as name
from persons p, _ids
where p.entityid = _ids.entityid)
order by relation, name

loop
        return next r;

end loop;

-- -------------------
--drop table _ids;

return;

END
$_$
    LANGUAGE plpgsql;


-- ALTER FUNCTION public.r_entities_idlist_name2(character varying) OWNER TO $1;

--
-- Name: r_entities_relname(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION r_entities_relname(integer) RETURNS name
    AS $_$select c.relname
from entities e, pg_class c
where entityid = $1
and e.tableoid = c.oid
$_$
    LANGUAGE sql;


-- ALTER FUNCTION public.r_entities_relname(integer) OWNER TO $1;

--
-- Name: rfi_primaryentityid(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION rfi_primaryentityid() RETURNS "trigger"
    AS $$   DECLARE   BEGIN   IF NEW.primaryentityid IN (select entityid from entities)       OR NEW.primaryentityid = NEW.entityid THEN      RETURN NEW;   ELSE      RAISE EXCEPTION       'insert or update on table "%" violates foreign key constraint for entities table',TG_RELNAME;   END IF;   END;   $$
    LANGUAGE plpgsql;


-- ALTER FUNCTION public.rfi_primaryentityid() OWNER TO $1;

--
-- Name: rfi_primaryentityid_delete(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION rfi_primaryentityid_delete() RETURNS "trigger"
    AS $$   DECLARE   BEGIN   IF OLD.entityid NOT IN (select primaryentityid from entities where entityid <> OLD.entityid ) THEN      RETURN OLD;   ELSE      RAISE EXCEPTION       'delete on table "%" violates foreign key constraint for entities table', TG_RELNAME;   END IF;   END;   $$
    LANGUAGE plpgsql;


-- ALTER FUNCTION public.rfi_primaryentityid_delete() OWNER TO $1;

--
-- Name: w_groupids_new(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION w_groupids_new() RETURNS integer
    AS $$DECLARE
	vgroupid int;
begin
	select into vgroupid nextval('groupids_groupid_seq');
	insert into groupids (groupid) values (vgroupid);
	return vgroupid;
end




$$
    LANGUAGE plpgsql;


-- ALTER FUNCTION public.w_groupids_new() OWNER TO $1;

--
-- Name: w_mailingids_create(text, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION w_mailingids_create(text, text) RETURNS integer
    AS $_$DECLARE
        veqxml alias for $1;
        veqsql alias for $2;
        vgroupid int;               -- Resulting ID for Mailing List
        sql text;                      -- SQL we'll put together to insert records
BEGIN

select into vgroupid nextval('groupids_groupid_seq');
insert into mailingids
(groupid, name, created, equery) values
(vgroupid, 'Mailing', now(), veqxml);

perform dropsilent('_ids');
sql = '
create temporary table _ids (entityid int);
insert into _ids (entityid) ' || veqsql;

EXECUTE sql;

sql = 'select vgroupid, entityid from _ids';

-- Insert into Mailing List
insert into mailings (groupid, entityid) EXECUTE sql;
drop table _ids;

-- Return Mailing List ID we created.
return vgroupid;

END$_$
    LANGUAGE plpgsql;


-- ALTER FUNCTION public.w_mailingids_create(text, text) OWNER TO $1;

--
-- Name: w_mailingids_create_old(text, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION w_mailingids_create_old(text, text) RETURNS integer
    AS $_$DECLARE
        veqxml alias for $1;
        veqsql alias for $2;
        vgroupid int;               -- Resulting ID for Mailing List
        sql text;                      -- SQL we'll put together to insert records
BEGIN

-- Create Mailing List
select into vgroupid nextval('groupids_groupid_seq');
insert into mailingids
(groupid, name, created, equery) values
(vgroupid, 'Mailing', now(), veqxml);

-- Create temporary table of IDs for this mailing list
perform dropsilent('_ids');
create temporary table _ids (entityid int);
delete from _ids;
sql := 'insert into _ids (entityid) ' || veqsql;
execute sql;

-- Insert into Mailing List
insert into mailings (groupid, entityid) select vgroupid, entityid from _ids;
drop table _ids;

-- Return Mailing List ID we created.
return vgroupid;

END
$_$
    LANGUAGE plpgsql;


-- ALTER FUNCTION public.w_mailingids_create_old(text, text) OWNER TO $1;

--
-- Name: w_mailings_correctlist(integer, boolean); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION w_mailings_correctlist(integer, boolean) RETURNS void
    AS $_$DECLARE
	vgroupid alias for $1;			-- Mailing to work on
	vkeepnosend alias for $2;	-- Keep "no send" people from list?
BEGIN
	-- Clear...
	update mailings set addressto = null
	where groupid = vgroupid;

	-- Send to the primary
	update mailings
	set sendentityid = e.primaryentityid, ename = null
	from entities e
	where mailings.entityid = e.entityid
	and mailings.groupid = vgroupid;

	-- Eliminate duplicates
	update mailings
	set minoid = xx.minoid
	from (
		select sendentityid, min(oid) as minoid
		from mailings m
		where m.groupid = vgroupid
		group by sendentityid
	) xx
	where mailings.sendentityid = xx.sendentityid
	and groupid = vgroupid;

	delete from mailings
	where groupid = vgroupid
	and oid <> minoid;

	-- Keep "no send" people
	if (not vkeepnosend) then
		update mailings
		set groupid = -2
		from entities e
		where mailings.sendentityid = e.entityid
		and not e.sendmail
		and mailings.groupid = vgroupid;

		delete from mailings where groupid = -2;
	end if;
	
	-- ========= Set addressto from multiple sources
	-- Set addressto by custom address to
	update mailings
	set addressto = customaddressto
	from persons p
	where p.entityid = sendentityid
	and p.customaddressto is not null
	and addressto is null
	and mailings.groupid = vgroupid;

	-- Use pre-computed names
	update mailings
	set addressto = ename
	where addressto is null and ename is not null
	and groupid = vgroupid;

	-- Set addressto as name of person
	update mailings
	set addressto = 
		coalesce(p.firstname || ' ', '') ||
		coalesce(p.middlename || ' ', '') ||
		coalesce(p.lastname, '')
	from persons p
	where mailings.sendentityid = p.entityid
	and mailings.groupid = vgroupid
	and addressto is null;


	-- Set addressto as name of organization
	update mailings
	set addressto = p.name
	from organizations p
	where mailings.sendentityid = p.entityid
	and mailings.groupid = vgroupid
	and addressto is null;
	-- ==================================

	-- Set the rest of the address
	update mailings
	set address1 = e.address1,
	address2 = e.address2,
	city = e.city,
	state = e.state,
	zip = e.zip,
	country = e.country
	from entities e
	where mailings.sendentityid = e.entityid
	and mailings.groupid = vgroupid;

	return;
END








$_$
    LANGUAGE plpgsql;


-- ALTER FUNCTION public.w_mailings_correctlist(integer, boolean) OWNER TO $1;

--
-- Name: w_multiqueryids_new(character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION w_multiqueryids_new(character varying) RETURNS integer
    AS $_$DECLARE
	vname alias for $1;
	vgroupid int;
BEGIN
	select into vgroupid nextval('groupids_groupid_seq');
	insert into multiqueryids (groupid, name) values (vgroupid, vname);
	return vgroupid;
END




$_$
    LANGUAGE plpgsql;


-- ALTER FUNCTION public.w_multiqueryids_new(character varying) OWNER TO $1;

--
-- Name: w_organizations_new(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION w_organizations_new(integer) RETURNS integer
    AS $_$DECLARE
	iprimaryentityid alias for $1;
	vprimaryentityid int;
	ventityid int;
begin
select into ventityid nextval('entities_entityid_seq');

if iprimaryentityid = 0 then
	vprimaryentityid = ventityid;
else
	vprimaryentityid = iprimaryentityid;
end if;

insert into organizations
(isquery, entityid, primaryentityid, relprimarytypeid)
values
(false, ventityid, vprimaryentityid, 0);

return ventityid;
end





$_$
    LANGUAGE plpgsql;


-- ALTER FUNCTION public.w_organizations_new(integer) OWNER TO $1;

--
-- Name: w_persons_new(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION w_persons_new(integer) RETURNS integer
    AS $_$DECLARE
	iprimaryentityid alias for $1;
	vprimaryentityid int;
	ventityid int;
begin
select into ventityid nextval('entities_entityid_seq');

if iprimaryentityid = 0 then
	vprimaryentityid = ventityid;
else
	vprimaryentityid = iprimaryentityid;
end if;

insert into persons
(isquery, entityid, primaryentityid, relprimarytypeid)
values
(false, ventityid, vprimaryentityid, 0);

return ventityid;
end








$_$
    LANGUAGE plpgsql;


-- ALTER FUNCTION public.w_persons_new(integer) OWNER TO $1;

--
-- Name: w_queries_new_entities(character varying, character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION w_queries_new_entities(character varying, character varying) RETURNS integer
    AS $_$DECLARE
vfromentityid int;
vtoentityid int;
fromid int;
toid int;
begin
select into fromid relprimarytypeid
from relprimarytypes where name = 'From';

select into toid relprimarytypeid
from relprimarytypes where name = 'To';

select into vfromentityid nextval('entities_entityid_seq');
select into vtoentityid nextval('entities_entityid_seq');

insert into queries (fromentityid, toentityid, dtime, username, name)
values (vfromentityid, vtoentityid, now(), $1, $2);

insert into entities (isquery, entityid, primaryentityid, relprimarytypeid) values
(true, vfromentityid, vfromentityid, fromid);

insert into entities (isquery, entityid, primaryentityid, relprimarytypeid) values
(true, vtoentityid, vfromentityid, toid);


return vfromentityid;
end

$_$
    LANGUAGE plpgsql;


-- ALTER FUNCTION public.w_queries_new_entities(character varying, character varying) OWNER TO $1;

--
-- Name: w_queries_new_organizations(character varying, character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION w_queries_new_organizations(character varying, character varying) RETURNS integer
    AS $_$DECLARE
vfromentityid int;
vtoentityid int;
fromid int;
toid int;
begin
select into fromid relprimarytypeid
from relprimarytypes where name = 'From';

select into toid relprimarytypeid
from relprimarytypes where name = 'To';

select into vfromentityid nextval('entities_entityid_seq');
select into vtoentityid nextval('entities_entityid_seq');

insert into queries (fromentityid, toentityid, dtime, username, name)
values (vfromentityid, vtoentityid, now(), $1, $2);

insert into organizations (isquery, entityid, primaryentityid, relprimarytypeid) values
(true, vfromentityid, vfromentityid, fromid);

insert into organizations (isquery, entityid, primaryentityid, relprimarytypeid) values
(true, vtoentityid, vfromentityid, toid);


return vfromentityid;
end

$_$
    LANGUAGE plpgsql;


-- ALTER FUNCTION public.w_queries_new_organizations(character varying, character varying) OWNER TO $1;

--
-- Name: w_queries_new_persons(character varying, character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION w_queries_new_persons(character varying, character varying) RETURNS integer
    AS $_$
DECLARE
vfromentityid int;
vtoentityid int;
fromid int;
toid int;
begin
select into fromid relprimarytypeid
from relprimarytypes where name = 'From';

select into toid relprimarytypeid
from relprimarytypes where name = 'To';

select into vfromentityid nextval('entities_entityid_seq');
select into vtoentityid nextval('entities_entityid_seq');

insert into queries (fromentityid, toentityid, dtime, username, name)
values (vfromentityid, vtoentityid, now(), $1, $2);

insert into persons (isquery, entityid, primaryentityid, relprimarytypeid) values
(true, vfromentityid, vfromentityid, fromid);

insert into persons (isquery, entityid, primaryentityid, relprimarytypeid) values
(true, vtoentityid, vfromentityid, toid);


return vfromentityid;
end



$_$
    LANGUAGE plpgsql;


-- ALTER FUNCTION public.w_queries_new_persons(character varying, character varying) OWNER TO $1;

SET search_path = pg_catalog;

SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = true;



