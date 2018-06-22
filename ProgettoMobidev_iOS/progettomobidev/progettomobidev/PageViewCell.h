//
//  PageViewCell.h
//  Journal
//
//  Created by ianfire on 23/10/16.
//  Copyright © 2016 ianfire. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Element.h"
#import "AVFoundation/AVFoundation.h"

@interface PageViewCell : UICollectionViewCell<UITextViewDelegate>{
    AVPlayer *videoPlayer;
}

@property (weak, nonatomic) IBOutlet UILabel *textName;
@property (weak, nonatomic) IBOutlet UILabel *textHour;
@property (weak, nonatomic) IBOutlet UILabel *textPlace;
@property (weak, nonatomic) IBOutlet UIButton *buttonShare;
@property (weak, nonatomic) IBOutlet UIButton *placeButton;
@property (weak, nonatomic) IBOutlet UIImageView *pageBackground;
@property (weak, nonatomic) IBOutlet UIImageView *contentImage;
@property (weak, nonatomic) IBOutlet UITextView *contentText;
@property (weak, nonatomic) IBOutlet UIView *contentVideo;
@property (weak, nonatomic) IBOutlet UIButton *playPauseButton;
@property (weak, nonatomic) IBOutlet UIButton *expandButton;
@property (weak, nonatomic) IBOutlet UIImageView *stickersImage;

@property (copy, nonatomic) void (^didTapButtonBlock)(id sender);
@property (copy, nonatomic) void (^didTapButtonBlockSharing)(id sender);

- (void)setDidTapButtonBlock:(void (^)(id sender))didTapButtonBlock;
- (void)fillWithElement: (Element *) element;
- (void)setNoteEditable: (BOOL) editable;
- (void)showImage: (UIImage *)image;
- (void)showVideo:(NSURL *)videoUrl;
- (void) setLocation: (float)latitude andLongitude: (float) longitude;
@end
