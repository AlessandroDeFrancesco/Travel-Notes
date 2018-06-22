//
//  ViewController.h
//  Travel Notes
//
//  Created by gianpaolo on 28/09/16.
//  Copyright © 2016 capraraedefrancescosoft. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <FBSDKLoginKit/FBSDKLoginKit.h>

@interface LoginViewController : UIViewController<FBSDKLoginButtonDelegate>

@property (weak, nonatomic) IBOutlet FBSDKLoginButton *loginButton;

-(void) downloadJournals;
@end

