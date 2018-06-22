//
//  FacebookFriend.m
//  Travel Notes
//
//  Created by gianpaolo on 06/10/16.
//  Copyright © 2016 capraraedefrancescosoft. All rights reserved.
//

#import "FacebookFriend.h"

@implementation FacebookFriend

// getter e setter
@synthesize name,idFriend,checked;

// implementazione dei costruttori
- (FacebookFriend*) initWithName: (NSString*) newName andIdFriend: (NSString*) newIdFriend {
    self = [super init];
    if (self){
        name = newName;
        idFriend = newIdFriend;
    }
    return self;
}

@end