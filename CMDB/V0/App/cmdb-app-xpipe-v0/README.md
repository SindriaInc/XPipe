Copyright Tecnoteca Srl 2005-2023


CMDBuild SUITE
==============
* CMDBuild - The platform for Asset Management - https://www.cmdbuild.org
* CMDBuild READY2USE - IT Assets & Services Management - https://www.cmdbuildready2use.org
* openMAINT - Property & Facility Management - https://www.openmaint.org

Maintainer: Tecnoteca - https://www.tecnoteca.com



LICENSE
=======
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
See the GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License 
along with this program.
If not, see <http://www.gnu.org/licenses/agpl.html>.



COPYRIGHT AND OWNERSHIP
=======================
Title, copyrights, intellectual property rights, international treaty, 
other rights as applicable and all other legal rights in the Software 
and Documentation are and shall remain the sole and exclusive property 
of Tecnoteca srl or its suppliers. 
As well all modifications, enhancements, derivatives and other 
alterations of the Software and Documentation regardless of who made 
any modifications, if any, are and shall remain the sole and exclusive 
property of Tecnoteca srl or its suppliers. 
The license for use granted herein is limited to the Software and 
Documentation and does not transfer any ownership rights from Tecnoteca 
to End Users or any other intellectual property rights.  


LEGAL NOTICES
=============
The interactive user interfaces in modified source and object code 
versions of this program must display Appropriate Legal Notices, as 
required under Section 5 of the GNU General Public License version 3.



TRADEMARKS
==========
CMDBuild, CMDBuild READY2USE, openMAINT trademarks can't be altered 
(colors, font, shape, etc) and can't be included in other trademarks.
CMDBuild, CMDBuild READY2USE, openMAINT trademarks can't be used as 
a company logo, moreover no company can act as CMDBuild or
CMDBuild READY2USE or openMAINT author/owner/maintainer.
CMDBuild, CMDBuild READY2USE, openMAINT trademarks can't be removed 
from the parts of the application in which are reported, and in 
particular from the header at the top of each page.

In accordance with Section 7(b) of the GNU General Public License 
version 3, these Appropriate Legal Notices must retain the display 
of the "CMDBuild", "CMDBuild READY2USE" or "openMAINT" logo.

The Logo "CMDBuild" must be a clickable link that leads directly 
to the Internet URL https://www.cmdbuild.org

The Logo "CMDBuild READY2USE" must be a clickable link that leads directly 
to the Internet URL https://www.cmdbuildready2use.org

The Logo "openMAINT" must be a clickable link that leads directly 
to the Internet URL https://www.openmaint.org


-------

Hardware minimum requirements, for simple use cases:
* server-class computer (modern architecture)
* 16 GB of RAM
* 150 GB of available hard disk space for each CMDBuild instance (SSD recommended)
* 4-core

Hardware recommended requirements, separated instances for all software services:
* 16 GB of RAM for CMDBuild intances, 50GB SSD recommended, 8-core
* 16 GB of RAM for PostgreSQL services, 500GB SSD recommended, 8-core
* for all other services, follow them recommended requirements

Software requirements: 

* any OS able to handle the following applications (linux recommended)
* PostgreSQL 12.x
* PostGIS 3.3.x (optional)
* Apache Tomcat 9.0 (9.0.75 recommended)
* Java 17 (OpenJDK recommended)
* Any DMS that supports the CMIS protocol (Alfresco Community recommended, optional)
* Geoserver 2.16.2 (optional)
* BIMServer 1.5.138 (optional)


Included libraries:

* jdbc library for DB connection
* jasperreports libraries for report generation
* CMIS DMS client
* Ext JS libraries for user interface
* Server and client components for map making feature
* Server and client components for BIM viewer (xeokit and BIMSurfer)


Additional software that you may find useful (not included):

* JasperSoft Studio for custom report design
* Together Workflow Editor for custom workflow design
* OCS Inventory as automatic inventory software


Deployment instructions:

Before deployment make sure you have satisfied all the requirements above (expecially
the postgres database and java environment).

CMDBuild can be deployed inside tomcat as any regular war-packaged webapp. 

Database configuration: it is recommended to create a configuration directory under `<tomcat>/conf/<webapp_name>` 
(the name of config directory must match the name of webapp deployment). 
You will find an example of db configuration inside `<webapp>/WEB-INF/conf/database.conf_example`. 
You must copy this to `database.conf` (either in config dir `<tomcat>/conf/<webapp_name>` or `<webapp>/WEB-INF/conf/`) and
edit it according to your database params.

A supported database has to be loaded before running the application, in the downloaded files there are different database
dumps you can use:
 * emtpy.dump.xz: a database dump with a basic structure and some minimal informations like a default admin account;
 * demo.dump.xz: a database dump with a basic structure and some more informations added in the system, like example users;

Guided setup: if you want to perform a clean installation you may use the graphic wizard (either run the cmdbuild.sh
installer or run `java -jar cmdbuild.war` directly from the war). By following the guided setup the database dump will be 
automatically loaded based on the dump you choose in the configuration phase. 
Use ready2use.sh or ready2use.war for CMDBuild READY2USE and openmaint.sh or openmaint.war for openMAINT.

For further information please refer to the Technical Manual (https://www.cmdbuild.org/en/documentation/manuals/technical-manual).


Available languages for CMDBuild project (first level localization):

* English
* Arabic
* Brazilian Portuguese
* Bulgarian
* Chinese
* Croatian
* Czech
* Danish
* Dutch
* European Portuguese
* French
* German
* Greek
* Hungarian
* Indonesian
* Italian
* Japanese
* Korean
* Malay
* Mongolian
* Norwegian
* Persian
* Polish
* Romanian
* Russian
* Serbian (Latin)
* Serbian (Cyrillic)
* Slovak
* Slovenian
* Spanish
* Turkish
* Ukrainian
* Vietnamese


External contributors: 

* Arabic translation by Mohammed Nabigh and Fahad Senan
* Brazilian Portuguese translation by T4HD
* Bulgarian translation by Yasen Arsov
* Chinese translation by Liansheng Yang
* Croatian translation by Tomislav Perić
* Czech translation by Igor Kurty
* Danish translation by Ali Araghi
* Dutch translation by Eric van Rheenen and Jeroen Baten
* European Portuguese translation by T4HD
* French translation by Pierre Danel and Tecnoteca Team
* German translation by Susanne Tober
* Greek translation by Vasilis Papadakis and Dimitris Maniadakis
* Hungarian translation by Márton Natkó
* Indonesian translation by Inovamap
* Japanese translation by Satoru Funai
* Korean translation by Kyungik An
* Malay translation by Openstack Sdn Bhd
* Mongolian translation by Tsolmonbaatar Adiyasuren
* Norwegian translation by Lars Tangen and Audun Wangen
* Persian translation by Mohsen Salami
* Polish translation by Mike Gleczman and Sebastian Hajdus
* Romanian translation by Adrian Popescu and Tecnoteca Team
* Russian translation by Yasen Arsov
* Serbian (Latin and Cyrillic script) translation by Miroslav Zaric
* Slovak translation by Igor Kurty
* Slovenian translation by Tine Cus
* Turkish translation by Onur Guleryuz
* Ukrainian translation by Artem Yanchuk
* Vietnamese translation by Avenue JSC

For updated information please refer to https://www.cmdbuild.org/en/download/available-languages


Available languages for CMDBuild READY2USE (second level localization):
Please refer to https://www.cmdbuildready2use.org/en/download/available-languages


Available languages for openMAINT (second level localization):
Please refer to https://www.openmaint.org/en/download/available-languages


Below is a list of the publicly available software and resources used
by and distributed with CMDBuild, along with the licensing terms.

* Ace (Ajax.org Cloud9 Editor) (https://ace.c9.io) is
 released under the BSD License:
   https://raw.githubusercontent.com/ajaxorg/ace/master/LICENSE

* FullCalendar (https://fullcalendar.io) is released under the MIT License:
   https://github.com/fullcalendar/fullcalendar/blob/master/LICENSE.txt
