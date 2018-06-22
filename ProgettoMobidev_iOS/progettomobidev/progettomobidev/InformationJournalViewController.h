//
//  InformationJournalViewController.h
//  Travel Notes
//
//  Created by gianpaolo on 30/10/16.
//  Copyright © 2016 capraraedefrancescosoft. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <FBSDKShareKit/FBSDKShareKit.h>

#import "JournalID.h"

@interface InformationJournalViewController : UIViewController<UIPickerViewDataSource , UIPickerViewDelegate, UITableViewDelegate, UITableViewDataSource, FBSDKAppInviteDialogDelegate>{
    @public
    JournalID *journalIDUpdated;
}

@property (strong, nonatomic) IBOutlet UIView *viewInformationJournal;
@property (weak, nonatomic) IBOutlet UITextField *journalName;
@property (weak, nonatomic) IBOutlet UITextField *journalDescription;
@property (weak, nonatomic) IBOutlet UITextField *textPlace;
@property (weak, nonatomic) IBOutlet UIPickerView *journalTypePicker;
@property (weak, nonatomic) IBOutlet UITableView *facebookFriendsAlreadyAddedTable;
@property (weak, nonatomic) IBOutlet UITableView *facebookFriendsNotAddedTable;
@property (weak, nonatomic) IBOutlet UIView *tableFriendsStillNotAdded;
@property (weak, nonatomic) IBOutlet UIButton *buttonUpdate;
@property (weak, nonatomic) IBOutlet UIView *viewFriendAlreadyAdded;
@property (weak, nonatomic) IBOutlet UITextField *textDeparture;
@property (weak, nonatomic) IBOutlet UITextField *textReturn;

@end
