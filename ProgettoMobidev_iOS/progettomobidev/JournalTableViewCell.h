//
//  MealTableViewCell.h
//  Storyboard
//
//  Created by gianpaolo on 20/10/16.
//  Copyright © 2016 caprara. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface JournalTableViewCell : UITableViewCell

// MARK: Properties
@property (weak, nonatomic) IBOutlet UILabel *JournalName;
@property (weak, nonatomic) IBOutlet UILabel *JournalDescription;
@property (weak, nonatomic) IBOutlet UILabel *JournalOwner;
@property (weak, nonatomic) IBOutlet UILabel *DepartureDateJournal;
@property (weak, nonatomic) IBOutlet UILabel *ReturnDateJournal;

@end
