//
//  UIViewController_CreateJournalViewController.h
//  Travel Notes
//
//  Created by ianfire on 16/10/16.
//  Copyright © 2016 capraraedefrancescosoft. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <FBSDKShareKit/FBSDKShareKit.h>

#import "JournalID.h"
#import "Travel_Notes-Swift.h"

@interface CreateJournalViewController : UIViewController<UIPickerViewDataSource, UIPopoverPresentationControllerDelegate , UIPickerViewDelegate, UITableViewDelegate, UITableViewDataSource, FBSDKAppInviteDialogDelegate>{
    @public
    JournalID * newJournalID;
    NSString* stringFromWall;
}


@property (weak, nonatomic) IBOutlet UITextField *textNameJournal;
@property (weak, nonatomic) IBOutlet UITextField *textDescriptionJournal;
@property (weak, nonatomic) IBOutlet UIPickerView *pickerTypeJournal;
@property (weak,nonatomic) IBOutlet SearchTextField* textPlaces;
@property (weak, nonatomic) IBOutlet UITableView *friendsTable;
@property (weak, nonatomic) IBOutlet UIButton *buttonDeparture;
@property (weak, nonatomic) IBOutlet UIButton *buttonReturn;
@property (weak, nonatomic) IBOutlet UITextField *textDeparture;
@property (weak, nonatomic) IBOutlet UITextField *textReturn;
@property (weak, nonatomic) IBOutlet UIScrollView *scrollView;
@property (weak, nonatomic) IBOutlet UIButton *createButton;

-(void) dateChosen: (NSDate *) date;

@end
