//
//  FriendsTableViewCell.h
//  Travel Notes
//
//  Created by gianpaolo on 26/10/16.
//  Copyright © 2016 capraraedefrancescosoft. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface FriendsTableViewCell : UITableViewCell{
    @public
    bool check;
}

@property (nonatomic) bool check;
@property (weak, nonatomic) IBOutlet UILabel *friendName;
@property (weak, nonatomic) IBOutlet UIButton *checkBox;

@end
