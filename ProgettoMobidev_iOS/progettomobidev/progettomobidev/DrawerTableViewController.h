//
//  DrawerTableViewController.h
//  Travel Notes
//
//  Created by gianpaolo on 25/10/16.
//  Copyright © 2016 capraraedefrancescosoft. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface DrawerTableViewController : UITableViewController

@property (weak, nonatomic) IBOutlet UILabel *profileLabel;
@property (weak, nonatomic) IBOutlet UILabel *createJournalLabel;
@property (weak, nonatomic) IBOutlet UILabel *journalsLabel;
@property (weak, nonatomic) IBOutlet UILabel *settingsLabel;
@property (weak, nonatomic) IBOutlet UIImageView *facebookImageProfile;
@end
