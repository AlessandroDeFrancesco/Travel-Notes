//
//  PopUpDatePickerViewController.h
//  Travel Notes
//
//  Created by ianfire on 09/11/16.
//  Copyright © 2016 capraraedefrancescosoft. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "CreateJournalViewController.h"

@interface PopUpDatePickerViewController : UIViewController
@property (weak, nonatomic) IBOutlet UIDatePicker *datePicker;
@property (weak, atomic) CreateJournalViewController *createJournalViewController;
@end
