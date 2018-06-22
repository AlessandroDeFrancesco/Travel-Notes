//
//  MealTableViewController.h
//  Storyboard Travel Notes
//
//  Created by Gianpaolo Caprara on 20/10/16.
//  Copyright © 2016 Gianpaolo Caprara. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "JournalTableViewCell.h"
#import "JournalID.h"

@interface JournalsViewController : UITableViewController{
    @public
    JournalID* journalIDselected;
    NSString* stringFromWall;
}

@end



