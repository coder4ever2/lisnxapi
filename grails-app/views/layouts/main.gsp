<!DOCTYPE html>
<html>
    <head>
        <title><g:layoutTitle default="LISNx" /></title>
        <link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
        <g:layoutHead />
        <g:javascript library="application" />
    </head>
    <body>
        <div id="spinner" class="spinner" style="display:none;background-color:#053055;">
            <img src="${resource(dir:'images',file:'spinner.gif')}" alt="${message(code:'spinner.alt',default:'Loading...')}" />
        </div>
        <div id="grailsLogo">
       
                                 <div align="center" valign="middle" style="font-family: ubuntu, arial, sans-serif; font-size: 12px;color: #303030;text-align:right; padding-right:10px;">
                                   

                                    <table width="100%" align="left" border="0" cellpadding="0" cellspacing="0" style="background-color:#053055;height:70px;">
                                       <tbody>
                                          <tr>
                                             <td width="20"></td>
                                             <td width="174" height="40" align="left">
                                                <div class="imgpop">
                                                   <a target="_blank" href="http://www.lisnx.com/">
                                                   <g:img base="http://www.lisnx.com/mobile" dir="img" file="logo.png" alt="App Store" border="0" width="180" height="56" style="display:block; border:none; outline:none; text-decoration:none;">
                                                   </g:img>
                                                </div>
                                             </td>
                                         
                                             <td width="22" height="22" align="left">
                                                <div class="imgpop">
                                                   <a target="_blank" href="https://www.facebook.com/pages/LISNx/276495852372110">
                                                   <img src="http://www.lisnx.com/mobile/img/facebook.png" alt="" border="0" width="22" height="22" style="display:block; border:none; outline:none; text-decoration:none;">
                                                   </a>
                                               		
                                                </div>
                                             </td>
                                             <td align="left" width="20" style="font-size:1px; line-height:1px;">&nbsp;</td>
                                          </tr>
                                       </tbody>
                                    </table>
                                    <!-- end of social icons -->
                                    </div>
                                 
        </div>
        <g:layoutBody />
    </body>
</html>