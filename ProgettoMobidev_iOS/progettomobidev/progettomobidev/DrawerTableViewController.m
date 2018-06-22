//
//  DrawerTableViewController.m
//  Travel Notes
//
//  Created by gianpaolo on 25/10/16.
//  Copyright © 2016 capraraedefrancescosoft. All rights reserved.
//

#import <FBSDKCoreKit/FBSDKCoreKit.h>
#import <FBSDKLoginKit/FBSDKLoginKit.h>
#import "DrawerTableViewController.h"
#import "WallViewController.h"

@interface DrawerTableViewController ()

@end

@implementation DrawerTableViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    // Configure Labels
    //facebookID = your facebook user id or facebook username both of work well
    NSURL *pictureURL = [NSURL URLWithString:[NSString stringWithFormat:@"https://graph.facebook.com/%@/picture?type=large&return_ssl_resources=1", [[FBSDKProfile currentProfile] userID]]];
    NSData *imageData = [NSData dataWithContentsOfURL:pictureURL];
    UIImage *fbImage = [UIImage imageWithData:imageData];
    [self.facebookImageProfile setImage:fbImage];
    
    // prendo nome e cognome
    NSString *name =[FBSDKProfile currentProfile].firstName;
    NSString *surname =[FBSDKProfile currentProfile].lastName;
    NSString *fullName = [NSString stringWithFormat:@"%@\n%@" ,name,surname];
    [self.profileLabel setText:fullName];
    
    // Do any additional setup after loading the view.
}

-(void) viewDidAppear:(BOOL)animated{
    self.tableView.tableFooterView = [[UIView alloc] initWithFrame:CGRectZero];
}
- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return 4;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    if (indexPath.row == 1){
        NSLog(@"Go To Create Journal");
        [self.mm_drawerController toggleDrawerSide:MMDrawerSideLeft animated:NO completion:nil];
        [self.mm_drawerController.centerViewController performSegueWithIdentifier:@"fromWallToCreateJournal" sender:self];
    } else if (indexPath.row == 2){
        NSLog(@"Go To Journals");
        [self.mm_drawerController toggleDrawerSide:MMDrawerSideLeft animated:NO completion:nil];
        [self.mm_drawerController.centerViewController performSegueWithIdentifier:@"fromWallToJournals" sender:self];
    } else if (indexPath.row == 3){
        NSLog(@"Go To Settings");
        [self.mm_drawerController toggleDrawerSide:MMDrawerSideLeft animated:NO completion:nil];
        [self.mm_drawerController.centerViewController performSegueWithIdentifier:@"fromWallToSettings" sender:self];
    }
}


/*
 #pragma mark - Navigation
 
 // In a storyboard-based application, you will often want to do a little preparation before navigation
 - (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
 // Get the new view controller using [segue destinationViewController].
 // Pass the selected object to the new view controller.
 }
 */
@end
