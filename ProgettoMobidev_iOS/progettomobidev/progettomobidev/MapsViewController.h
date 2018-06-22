//
//  MapsViewController.h
//  Travel Notes
//
//  Created by gianpaolo on 11/10/16.
//  Copyright © 2016 capraraedefrancescosoft. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <FBSDKCoreKit/FBSDKCoreKit.h>

#import "Element.h"
#import "JournalID.h"
@import GoogleMaps;

@interface MapsViewController : UIViewController<GMSMapViewDelegate>{
    @public
    Element *element;
    JournalID *journalID;
}

@end

