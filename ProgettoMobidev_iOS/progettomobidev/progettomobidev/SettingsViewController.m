//
//  SettingsViewController.m
//  Travel Notes
//
//  Created by gianpaolo on 19/10/16.
//  Copyright © 2016 capraraedefrancescosoft. All rights reserved.
//

#import <FBSDKCoreKit/FBSDKCoreKit.h>
#import <FBSDKLoginKit/FBSDKLoginKit.h>
#import "SettingsViewController.h"
#import "LoginViewController.h"

@implementation SettingsViewController

- (void)viewDidLoad {
    self.navigationItem.title = @"Settings";
    [super viewDidLoad];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

-(void) viewDidAppear:(BOOL)animated{
    self.tableView.tableFooterView = [[UIView alloc] initWithFrame:CGRectZero];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return 2;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    if (indexPath.row == 1){
        NSLog(@"Logout from application");
        
        UIAlertController *logoutController = [UIAlertController alertControllerWithTitle:@"Logout" message:@"Are you sure to Logout from Application?" preferredStyle:UIAlertControllerStyleAlert];
        UIAlertAction* yesButton = [UIAlertAction actionWithTitle:@"Yes" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
            // logout from application
            FBSDKLoginManager *loginManager = [[FBSDKLoginManager alloc] init];
            [loginManager logOut];
            
            //andare nella pagina principale dell'app
            UIStoryboard* storyboard = [UIStoryboard storyboardWithName:@"TravelNotesStoryboard"
                                                                 bundle:nil];
            UIViewController *loginWiev =  [storyboard instantiateViewControllerWithIdentifier:@"LOGIN_NAVIGATION_CONTROLLER"];
            [self presentViewController:loginWiev animated:YES completion:nil];
        }];
        
        UIAlertAction* noButton = [UIAlertAction actionWithTitle:@"No" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
            
        }];
        
        [logoutController addAction:yesButton];
        [logoutController addAction:noButton];
    
        [self presentViewController:logoutController animated:YES completion:nil];
    }
}

- (IBAction)switchPreferenceNotification:(id)sender {
    UISwitch *notification_switch = (UISwitch*) sender;
    if([notification_switch isOn]){
        NSLog(@"Notification enabled");
    } else {
        NSLog(@"Notification disabled");
    }
}

@end
