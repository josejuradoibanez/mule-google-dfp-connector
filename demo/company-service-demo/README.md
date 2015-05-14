Google-DFP Company Service Demo
===============================

INTRODUCTION
------------

This demo shows how you can use the get-agency-by-name and update-company operations to update a Company in Google DFP.

HOW TO RUN DEMO
---------------

### Prerequisites
In order to build run this demo project you'll need:

* Anypoint Studio with Mule ESB 3.6 Runtime
* Google-DFP Connector v1.0.0 installed on Studio
* Google-DFP Credentials

### Importing the Demo

With Anypoint Studio up and running, open the Import wizard from the File menu. A pop-up wizard will offer you the chance to pick Anypoint Studio Project from External Location. On the next wizard window point Project Root to the location of the demo project directory and select the Server Runtime as Mule Server 3.6.1. Once successfully imported the studio will automatically present the Mule Flows.

From the Package Explorer view, navigate to src/main/resources and open demo.properties. Fill in your Google-DFP credentials.

Note that the Google-DFP connector library may not have been added automatically to the project build path. To add the library, open the Mule Flow, right-click on a Google-DFP connector on the flow and click 'Add GoogleDFP libraries to the project'.

### Running the Create Company Demo

Run the demo as a Mule Application and in your browser hit this endpoint: **http://localhost:8081/createCompany**.

Congratulations, your company has been created!

### Running the Update Company Demo

Run the demo as a Mule Application and in your browser hit this endpoint: **http://localhost:8081/updateCompany?name=\<oldCompanyName\>&newname=\<newCompanyName\>**.

Congratulations, your company has been updated!

